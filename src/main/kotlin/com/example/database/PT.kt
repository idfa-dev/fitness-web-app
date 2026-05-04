// PT entity, mapping onto PTs

package com.example.database

import org.jetbrains.exposed.v1.jdbc.transactions.transaction

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class PT(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PT>(PTs) {
        
        // mini explanation
        // PT.create(username, password, string, email, fname, height, weight, dob, sex) <-- order matters
        // if not passing ALL of them in, then specify for all defaultable variables 
        // e.g. PT.create( username, password, email, _fname = fname, )
        fun create( _username: String, _password: String, _email: String, _fname: String = "", _height: Float = 0f, _weight: Float = 0f, _dob: String = "", _sex: String = ""){ 
            transaction{
                PT.new{
                    username = _username        //First three are mandatory
                    password = _password
                    email = _email
                    fname = _fname              //These values can be defaulted
                    height = _height            //These values can be defaulted
                    weight = _weight            //These values can be defaulted
                    dob = _dob                  //These values can be defaulted
                    sex = _sex                  //These values can be defaulted
                }
            }
        }

        // Either pass all parameters in order, or reference which are being passed specifically ( id is necessary though )
        // e.g. PT.modify( id, _username = newuser, _email = newemail )
        // or PT.modify( id, _password = regulardude321 )
        fun modify( id: Int, _username: String? = null,  _password: String? = null,  _email: String? = null,  _fname: String? = null,  _height: Float? = null,  _weight: Float? = null,  _dob: String? = null,  _sex: String? = null) {

            transaction {
                val pt = PT.findById(id) ?: return@transaction // find PT/check if it actually exists

                // .let only changes if a non-null value is passed in
                _username?.let { pt.username = it }
                _password?.let { pt.password = it } // gotta fix for hashing later
                _email?.let { pt.email = it }
                _fname?.let { pt.fname = it }
                _height?.let { pt.height = it }
                _weight?.let { pt.weight = it }
                _dob?.let { pt.dob = it }
                _sex?.let { pt.sex = it }
            }

        }
    }

    var username by PTs.username
    var password by PTs.password
    var email by PTs.email
    var fname by PTs.fname
    var height by PTs.height
    var weight by PTs.weight
    var dob by PTs.dob
    var sex by PTs.sex

    override fun toString() = username

    
}