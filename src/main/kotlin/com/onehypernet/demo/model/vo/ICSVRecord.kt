package com.onehypernet.demo.model.vo

interface ICSVRecord {
    fun toRow(): Array<String>
}