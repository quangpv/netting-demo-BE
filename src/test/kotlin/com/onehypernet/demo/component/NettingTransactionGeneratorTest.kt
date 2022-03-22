//package com.onehypernet.demo.component
//
//import com.onehypernet.demo.model.enumerate.TransactionType
//import com.onehypernet.demo.model.vo.TransactionVO
//import com.onehypernet.model.FeeParam
//import com.onehypernet.netting.optimize.ParameterLookup
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//internal class NettingTransactionGeneratorTest {
//    private val generator = NettingTransactionGeneratorImpl(NettingIdGenerator())
//    private val csvReader: CSVReader = CSVReader()
//
//    @BeforeEach
//    fun before() {
//    }
//
//    private fun loadTransactions(): List<TransactionVO> {
//        return csvReader.readStream(javaClass.getResourceAsStream("/transactions.csv")!!) {
//            if (it[0].trim().toLowerCase() == "date") return@readStream null
//            TransactionVO(
//                id = it[2],
//                type = TransactionType[it[3]],
//                counterPartyName = it[4],
//                currency = it[5],
//                amount = it[6].toBigDecimal()
//            )
//        }
//    }
//
//    private fun loadFees(): List<FeeParam> {
//        return csvReader.readStream(javaClass.getResourceAsStream("/netting-params.csv")!!) {
//            if (it[0].trim().toLowerCase() == "from") return@readStream null
//            FeeParam(
//                fromCurrency = it[0].trim(),
//                toCurrency = it[1].trim(),
//                marginPercent = it[2].toDouble(),
//                feePercent = it[3].toDouble(),
//                minFee = it[4].toDouble(),
//                maxFee = it[5].toDouble(),
//                fixedFee = it[6].toDouble(),
//                exchangeRate = it[7].toDouble(),
//                location = it[8].trim(),
//                toLocations = it[9].trim(),
//            )
//        }
//    }
//
//    @Test
//    fun `print generated`() {
//        val expectedMinPartySize = 5
//        val expectedMinCurrencySize = 5
//
//        val generated = generator.generate(
//            "BankA", loadTransactions(), ForexLookup(
//                ParameterLookup(
//                    loadFees(),
//                    emptyList()
//                )
//            )
//        )
//
//        generated.forEach { println(it) }
//
//        val numOfParty = generated.flatMap { listOf(it.fromPartyId, it.toPartyId) }.distinct().size
//        val numOfCurrency = generated.map { it.currency }.distinct().size
//
//        assert(numOfParty >= expectedMinPartySize) {
//            "Expect num of party $expectedMinPartySize but got $numOfParty"
//        }
//
//        assert(numOfCurrency >= expectedMinCurrencySize) {
//            "Expect num of currency $expectedMinCurrencySize but got $numOfCurrency"
//        }
//    }
//}