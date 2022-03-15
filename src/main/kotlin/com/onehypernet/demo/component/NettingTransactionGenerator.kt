//package com.onehypernet.demo.component
//
//import com.onehypernet.demo.AppConst
//import com.onehypernet.demo.model.enumerate.TransactionType
//import com.onehypernet.demo.model.vo.TransactionVO
//import com.onehypernet.model.NettingTransaction
//import com.onehypernet.netting.optimize.ParameterLookup
//import org.springframework.stereotype.Component
//import java.math.BigDecimal
//import kotlin.math.roundToLong
//import kotlin.random.Random
//
//interface NettingTransactionGenerator {
//    /**
//     * Guide for user
//    1. Upload a minimum of 10 transactions, 3 counterparties, and 2 currencies.
//    2. Only transactions with due dates later than the settlement date will be included for netting.
//
//    Rules to generate transactions
//    1. Minimum of 5 parties. If user upload 2 parties, total_parties = 3 (user + 2), so system generates 2 more parties.
//    These new parties should transact with each other and the parties uploaded by user, but not with the user (because we should not generate transactions for the user).
//    2. Minimum of 5 currencies. If user uploads 2 currencies, total_currencies = 2, so system generates 3 more currencies.
//    These new transactions should be generated between parties excluding the user (because we should not generate transactions for the user).
//    3. No convertibility for this demo.
//
//    Logic to generate transactions
//    1. Create a corresponding transaction for every transaction uploaded by user
//    e.g. if user uploads SGD 10,000 Payable to Bank B, create transaction for Bank B of SGD 10,000 receivable from user
//    2. Average amount to generate for each transaction is calculated from what the user uploads:
//    e.g. if user uploaded average transaction size = USD 100,000, we generate transactions with average of +200% -20% from USD 100,000
//    3. Select currencies to generate in this order (if needed)
//    SGD, CNY, INR, VND, HKD
//     */
//    fun generate(
//        partyId: String,
//        transactions: List<TransactionVO>,
//        converter: ForexConverter
//    ): List<NettingTransaction>
//}
//
//@Component("NettingTransactionGenerator")
//class NettingTransactionGeneratorImpl(
//    private val idGenerator: NettingIdGenerator
//) : NettingTransactionGenerator {
//    companion object {
//        val GENERATE_CURRENCIES_SUPPORTED = arrayOf("SGD", "CNY", "INR", "VND", "HKD")
//        val GENERATE_PARTNER_SUPPORTED = AppConst.DEFAULT_PARTIES
//        const val GENERATE_AMOUNT_MAX = 2
//        const val GENERATE_AMOUNT_MIN = -0.2
//
//        const val MINIMUM_CURRENCY_SIZE = 5
//        const val MINIMUM_PARTNER_SIZE = 5
//    }
//
//    override fun generate(
//        partyId: String,
//        transactions: List<TransactionVO>,
//        converter: ForexConverter
//    ): List<NettingTransaction> {
//        val partyStack = PartyStack()
//        val currencyStack = CurrencyStack()
//        val existedTransactionLookup = ExistedTransactionLookup(partyId, transactions)
//
//        fillPartyAndCurrencyToStack(partyStack, currencyStack, transactions, partyId)
//
//        addMorePartnerIfRequire(partyStack)
//
//        addMoreCurrencyIfRequire(currencyStack)
//
//        val (min, max) = calculateAmountRangeForGeneratedTransaction(converter, transactions)
//
//        val pairedPartyStack = generatePartyPairs(partyStack)
//
//        return generateTransactions(
//            pairedPartyStack, currencyStack, min, max, converter, existedTransactionLookup
//        ) + existedTransactionLookup.getNettingTransactions()
//    }
//
//    private fun generateTransactions(
//        partyPairStack: PartyPairStack,
//        currencyStack: CurrencyStack,
//        min: Long,
//        max: Long,
//        converter: ForexConverter,
//        existedTransactionLookup: ExistedTransactionLookup
//    ): List<NettingTransaction> {
//        val transactions = arrayListOf<NettingTransaction>()
//
//        while (partyPairStack.hasNext()) {
//            val paired = partyPairStack.next().randomPair()
//            val currencies = currencyStack.clone()
//
//            while (currencies.hasNext()) {
//                val currency = currencies.next()
//
//                val shouldCreateTran = !existedTransactionLookup.exists(paired.withCurrency(currency))
//
//                if (shouldCreateTran) {
//                    val randomAmount = createRandomAmount(converter, min, max, currency, paired)
//                    val tran = createTransaction(paired, currency, randomAmount)
//                    transactions.add(tran)
//                }
//            }
//        }
//        return transactions
//    }
//
//    private fun createRandomAmount(
//        converter: ForexConverter,
//        min: Long,
//        max: Long,
//        currency: String,
//        paired: PartyPair
//    ): Double {
//        val usdAmount = Random.nextLong(min, max)
//        return converter
//            .getAmount(usdAmount, currency, paired.partyId)
//            .roundToLong().toDouble()
//    }
//
//    private fun createTransaction(
//        paired: PartyPair,
//        currency: String,
//        randomAmount: Double,
//    ): NettingTransaction {
//        return NettingTransaction(
//            fromPartyId = paired.partyId,
//            toPartyId = paired.counterPartyId,
//            amount = randomAmount,
//            currency = currency,
//            id = idGenerator.generateTxId()
//        )
//    }
//
//    private fun generatePartyPairs(partyStack: PartyStack): PartyPairStack {
//        val stack = PartyPairStack()
//        for (partyIndex in (0 until partyStack.size)) {
//            for (nextPartyIndex in (partyIndex + 1 until partyStack.size)) {
//                stack.push(PartyPair(partyStack[partyIndex], partyStack[nextPartyIndex]))
//            }
//        }
//        return stack
//    }
//
//    private fun calculateAmountRangeForGeneratedTransaction(
//        fxCalculator: ForexConverter,
//        transactions: List<TransactionVO>
//    ): Pair<Long, Long> {
//        val average = transactions.sumOf {
//            fxCalculator.getUsdAmount(it.amount, it.currency, it.counterPartyId)
//        } / transactions.size
//        return (average - (-GENERATE_AMOUNT_MIN * average)).roundToLong() to (GENERATE_AMOUNT_MAX * average).roundToLong()
//    }
//
//    private fun addMoreCurrencyIfRequire(currencyStack: CurrencyStack) {
//        addMoreIfRequire(currencyStack, MINIMUM_CURRENCY_SIZE, GENERATE_CURRENCIES_SUPPORTED)
//    }
//
//    private fun addMorePartnerIfRequire(stack: PartyStack) {
//        addMoreIfRequire(stack, MINIMUM_PARTNER_SIZE, GENERATE_PARTNER_SUPPORTED.map { it.id }.toTypedArray())
//    }
//
//    private fun addMoreIfRequire(stack: Stack<String>, minSize: Int, generated: Array<String>) {
//        if (stack.size < minSize) {
//            var needMore = minSize - stack.size
//            val extra = generated.random()
//            var index = 0
//            while (needMore > 0) {
//                if (stack.pushIfNeeded(extra[index++])) {
//                    needMore--
//                }
//            }
//        }
//    }
//
//    private fun fillPartyAndCurrencyToStack(
//        partyStack: PartyStack,
//        currencyStack: CurrencyStack,
//        transactions: List<TransactionVO>,
//        partyId: String
//    ) {
//        partyStack.pushIfNeeded(partyId)
//        transactions.forEach {
//            partyStack.pushIfNeeded(it.counterPartyId)
//            currencyStack.pushIfNeeded(it.currency)
//        }
//    }
//
//    private fun <T> Array<T>.random(): Array<T> {
//        val extra = clone()
//        extra.shuffle()
//        return extra
//    }
//
//    abstract class Stack<T>(initial: ArrayList<T> = arrayListOf()) {
//        private val mValue = ArrayList<T>(initial)
//
//        private var mPointer = -1
//        val size: Int get() = mValue.size
//        protected val value get() = mValue
//
//        fun pushIfNeeded(value: T): Boolean {
//            if (mValue.contains(value)) return false
//            mValue.add(value)
//            return true
//        }
//
//        fun addAll(list: List<T>) {
//            mValue.addAll(list)
//        }
//
//        fun hasNext(): Boolean {
//            if (mPointer + 1 <= size - 1) return true
//            return false
//        }
//
//        fun next(): T {
//            mPointer++
//            return mValue[mPointer]
//        }
//
//        fun push(value: T) {
//            mValue.add(value)
//        }
//
//        operator fun get(index: Int): T {
//            return mValue[index]
//        }
//    }
//
//    class PartyPairStack : Stack<PartyPair>()
//    class PartyStack : Stack<String>()
//
//    class CurrencyStack(initial: ArrayList<String> = arrayListOf()) : Stack<String>(initial) {
//        fun clone(): CurrencyStack {
//            return CurrencyStack(value)
//        }
//    }
//
//    class PartyPair(
//        val partyId: String,
//        val counterPartyId: String
//    ) {
//        fun withCurrency(currency: String): String {
//            return "${partyId}#${counterPartyId}#${currency}"
//        }
//
//        fun randomPair(): PartyPair {
//            return if (Random.nextBoolean()) PartyPair(counterPartyId, partyId) else this
//        }
//    }
//
//    private inner class ExistedTransactionLookup(
//        private val partyId: String,
//        private val transactions: List<TransactionVO>
//    ) {
//        private val mMap = hashMapOf<String, NettingTransaction>()
//
//        init {
//            transactions.forEach {
//                val counterPartyId = it.counterPartyId
//                val fromParty: String
//                val toParty: String
//
//                if (it.type == TransactionType.Payable) {
//                    fromParty = partyId
//                    toParty = counterPartyId
//                } else {
//                    fromParty = counterPartyId
//                    toParty = partyId
//                }
//
//                mMap[keyOf(fromParty, toParty, it.currency)] = NettingTransaction(
//                    id = it.id,
//                    fromPartyId = fromParty,
//                    toPartyId = toParty,
//                    amount = it.amount.toDouble(),
//                    currency = it.currency,
//                )
//            }
//        }
//
//        private fun keyOf(from: String, to: String, currency: String): String {
//            return "$from#$to#$currency"
//        }
//
//        fun exists(key: String): Boolean {
//            return mMap.containsKey(key)
//        }
//
//        fun getNettingTransactions(): Collection<NettingTransaction> {
//            return mMap.values
//        }
//    }
//}
//
//
//interface ForexConverter {
//    fun getAmount(usdAmount: Long, currency: String, partyId: String): Double
//    fun getUsdAmount(amount: BigDecimal, currency: String, partyId: String): Double
//}
//
//class ForexLookup(private val lookup: ParameterLookup) : ForexConverter {
//    override fun getAmount(usdAmount: Long, currency: String, partyId: String): Double {
//        return usdAmount * rateOf(AppConst.BRIDGING_CURRENCY, currency, partyId)
//    }
//
//    override fun getUsdAmount(amount: BigDecimal, currency: String, partyId: String): Double {
//        return amount.toDouble() * rateOf(currency, AppConst.BRIDGING_CURRENCY, partyId)
//    }
//
//    private fun rateOf(fromCurrency: String, toCurrency: String, partyId: String): Double {
//        if (fromCurrency == toCurrency) return 1.0
//
//        var rate = lookup.getExchangeRateOrNull(partyId, fromCurrency, toCurrency)
//        if (rate != null) return rate
//        rate = lookup.getExchangeRateOrNull(partyId, toCurrency, fromCurrency)
//            ?: error("Not found exchange rate from $fromCurrency to $toCurrency")
//        return 1 / rate
//    }
//}
//
//
//class DefaultPartner(
//    val id: String,
//    val name: String,
//    val countryCode: String,
//    val currency: String
//)