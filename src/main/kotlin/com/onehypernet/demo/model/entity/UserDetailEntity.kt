package com.onehypernet.demo.model.entity

import javax.persistence.*

@Entity(name = "user_detail")
class UserDetailEntity(

    @Id
    @Column(name = "user_id")
    var id: String = "",

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId
    var user: UserEntity? = null,

    var name: String = "",

    @Column(name = "country_code")
    var countryCode: String = "",

    @Column(name = "currency")
    var currency: String = "",

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "bank_id", referencedColumnName = "account_number")
    var bank: BankAccountEntity? = null
)

@Entity(name = "bank_account")
class BankAccountEntity(
    @Id
    @Column(name = "account_number")
    var accountNumber: String = "",

    @Column(name = "account_name")
    var accountName: String = "",

    var address: String = "",

    var swift: String = "",

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "bank")
    var detail: UserDetailEntity? = null
)