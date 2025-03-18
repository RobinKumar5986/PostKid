package com.kgJr.posKid

import com.kgJr.posKid.api.ApiHandler
import com.kgJr.posKid.api.ApiMethodType

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        /***
        * @POST: Request
        val apiHandler = ApiHandler()
        val body = """
            {
                "name": "morpheus",
                "job": "leader"
            }
        """.trimIndent()
        val response = apiHandler.callRequest(
            "https://reqres.in/api/users",
            ApiMethodType.POST,
            body
        )
        println("response")
        println(response)
        */


        /**
         * @GET: Request
        val apiHandler = ApiHandler()
        val body = """
            {
                "name": "morpheus",
                "job": "leader"
            }
        """.trimIndent()
        val response = apiHandler.callRequest(
            "https://reqres.in/api/users",
            ApiMethodType.GET,
            body
        )
        println("response")
        println(response)
        */


       /**
        * @PUT: Request
        val apiHandler = ApiHandler()
        val body = """
        {
            "name": "morpheus",
            "job": "zion resident"
        }
        """.trimIndent()
        val response = apiHandler.callRequest(
        "https://reqres.in/api/users/2",
        ApiMethodType.PUT,
        body
        )
        println("response")
        println(response)*/


      /**
       * @DELETE: Request
       val apiHandler = ApiHandler()
        val body = null
        val response = apiHandler.callRequest(
        "https://reqres.in/api/users/2",
        ApiMethodType.DELETE,
        body
        )
        println("response")
        println(response)*/

    }
}