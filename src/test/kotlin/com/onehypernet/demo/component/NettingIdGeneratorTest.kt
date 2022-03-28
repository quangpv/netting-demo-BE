package com.onehypernet.demo.component

import org.junit.jupiter.api.Test

class NettingIdGeneratorTest {
    @Test
    fun test() {
        fun expect(fromId: String, expectedId: String) {
            val nextId = NettingIdGenerator().generate(fromId)

            assert(nextId == expectedId) {
                "Expect $expectedId but $nextId"
            }
        }
        expect("NT00000001", "NT00000002")
        expect("NT00000002", "NT00000003")
    }
}