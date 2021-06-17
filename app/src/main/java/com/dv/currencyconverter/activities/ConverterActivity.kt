package com.dv.currencyconverter

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dv.currencyconverter.databinding.ActivityMainBinding
import com.dv.currencyconverter.databinding.ItemLisstBinding
import com.dv.currencyconverter.database.entities.CurrencyListEntity
import com.dv.currencyconverter.database.entities.ExchangeRateEntity

class ConverterActivity : AppCompatActivity(), CurrencyListErrorListener,
    Observer<List<CurrencyListEntity>>, AdapterView.OnItemSelectedListener,
    ExchangeListErrorListener, View.OnClickListener {

    private lateinit var mBinding: ActivityMainBinding
    private var mListAdapter: Adapter? = null
    private var mSpinnerAdapter: ArrayAdapter<CurrencyListEntity>? = null
    private lateinit var mModel: ConverterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        mListAdapter = Adapter(this)
        mBinding.list.layoutManager = GridLayoutManager(this, 2)
        mBinding.list.adapter = mListAdapter
        setContentView(mBinding.root)
        mModel = ViewModelProvider(this).get(ConverterViewModel::class.java)
        mBinding.spinner.onItemSelectedListener = this
        mBinding.btn.setOnClickListener(this)
        initCurrencyList()
    }

    private fun initCurrencyList() {
        mModel.getCurrencyList(this).observe(this, this)
        showProgress()
    }

    private fun showProgress() {
        mBinding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        mBinding.progress.visibility = View.GONE
    }

    override fun onChanged(t: List<CurrencyListEntity>?) {
        if (t?.isEmpty() == false) {
            hideProgress()
            mSpinnerAdapter = ArrayAdapter<CurrencyListEntity>(
                this@ConverterActivity,
                R.layout.item_spinner,
                R.id.txt,
                t!!
            )
            mBinding.spinner.adapter = mSpinnerAdapter
            mBinding.spinner.setSelection(getPreviuosSelectedPos(t))

        }
    }

    private fun getPreviuosSelectedPos(list: List<CurrencyListEntity>?): Int {
        if (list != null) {
            var selectedPos = 0
            for (entity in list) {
                if (entity.mCurrencyCode == mModel.mSourceCurrency) {
                    return selectedPos
                }
                ++selectedPos
            }
        }
        return 0
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mModel.mSourceCurrency = mSpinnerAdapter?.getItem(position)?.mCurrencyCode!!
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onCurrencyListApiFailed(error: String?) {
        hideProgress()
        showError(error)
    }

    override fun onExchangeRateApiFailed(error: String?) {
        hideProgress()
        showError(error)
    }

    private fun showError(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        if (TextUtils.isEmpty(mBinding.editText.text.toString())) {
            showError("Enter a valid amount")
        } else if (mModel.mSourceCurrency == null) {
            showError("Select a source currency")
            initCurrencyList()
        } else {
            showProgress()
            mListAdapter?.mMultiplyingFactor = mBinding.editText.text.toString().toDouble()
            mListAdapter?.mRateList = null
            mListAdapter?.notifyDataSetChanged()
            mModel.getExchangeRates(mModel.mSourceCurrency!!, this)
                .observe(this@ConverterActivity, object : Observer<List<ExchangeRateEntity>> {
                    override fun onChanged(t: List<ExchangeRateEntity>?) {
                        if (t?.isEmpty() == false) {
                            hideProgress()
                            mListAdapter?.mRateList = t
                            mListAdapter?.notifyDataSetChanged()
                        }
                    }

                })
        }
    }

}

class Adapter(context: Context) : RecyclerView.Adapter<VH>(), MultiplierSupplier {

    var mRateList: List<ExchangeRateEntity>? = null
    var mMultiplyingFactor: Double = 1.0
    val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemLisstBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return VH(binding, this)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(mRateList!![position])
    }

    override fun getItemCount(): Int {
        if (mRateList != null && mRateList?.isEmpty() == false) {
            return mRateList!!.size
        } else {
            return 0
        }
    }

    override fun getMultiplier(): Double {
        return mMultiplyingFactor
    }
}

class VH(binding: ItemLisstBinding, multiplier: MultiplierSupplier) :
    RecyclerView.ViewHolder(binding.root) {

    private val mBinding = binding
    private val mMutiplier = multiplier

    fun bind(rateEntity: ExchangeRateEntity) {
        mBinding.toCurrency.text = rateEntity.mToCurrencyCodeL
        mBinding.rate.text = (rateEntity.mRate * mMutiplier.getMultiplier()).toString()
    }

}

interface MultiplierSupplier {

    fun getMultiplier(): Double

}
