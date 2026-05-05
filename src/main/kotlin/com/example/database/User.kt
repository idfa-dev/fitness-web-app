// User entity, mapping onto Users

package com.example.database

import org.jetbrains.exposed.v1.jdbc.transactions.transaction

import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.core.dao.id.EntityID

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users) {
        
        // mini explanation
        // User.create(username, password, string, email, fname, height, weight, dob, sex) <-- order matters
        // if not passing ALL of them in, then specify for all defaultable variables 
        // e.g. User.create( username, password, email, _fname = fname, )
        fun create( _type: Int, _username: String, _password: String, _email: String, _fname: String = "", _height: Float = 0f, _weight: Float = 0f, _dob: String = "", _age: Int = 0, _sex: String = ""){ 
            transaction{
                User.new{
                    type = _type                //First four are mandatory
                    username = _username
                    password = _password
                    email = _email
                    fname = _fname              //These values can be defaulted ( left empty basically )
                    height = _height            //These values can be defaulted
                    weight = _weight            //These values can be defaulted
                    dob = _dob                  //These values can be defaulted
                    age = _age                  //These values can be defaulted
                    sex = _sex                  //These values can be defaulted
                }
            }
        }

        // Either pass all parameters in order, or reference which are being passed specifically ( id is necessary though )
        // e.g. User.modify( id, _username = newuser, _email = newemail )
        // or User.modify( id, _password = regulardude321 )
        fun modify( id: Int, _type: Int? = null, _username: String? = null, _password: String? = null, _email: String? = null, _fname: String? = null, _height:Float? = null,  _weight: Float? = null, _dob: String? = null, _age: Int? = null,  _sex: String? = null) {

            transaction {
                val user = User.findById(id) ?: return@transaction // find User/check if it actually exists

                // .let only changes if a non-null value is passed in
                _type?.let { user.type = it }
                _username?.let { user.username = it }
                _password?.let { user.password = it } // gotta fix for hashing later
                _email?.let { user.email = it }
                _fname?.let { user.fname = it }
                _height?.let { user.height = it }
                _weight?.let { user.weight = it }
                _dob?.let { user.dob = it }
                _age?.let { user.age = it}
                _sex?.let { user.sex = it }
            }

        }
    }

    var type by Users.type
    var username by Users.username
    var password by Users.password
    var email by Users.email
    var fname by Users.fname
    var height by Users.height
    var weight by Users.weight
    var dob by Users.dob
    var age by Users.age
    var sex by Users.sex

    override fun toString(): String {
        return "User(id=$id, type=$type, username=$username)"
    }
}