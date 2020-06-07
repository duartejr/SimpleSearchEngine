package search

import java.io.File
import java.util.*

fun main(args: Array<String>) {
    Search().menu(args)
    println("\nBye!")
}

class Search{
    private val scanner = Scanner(System.`in`)
    private var databaseName = "database.csv"
    private var database = File(databaseName)
    private val invertedIndex = mutableMapOf<String, MutableList<Int>>()
    private var lines = database.readLines()

    fun menu(args: Array<String>){
        if (args.isNotEmpty()){
            if (args[0] == "--data") {
                database = File(args[1])
                readIndex()
            }
        } else {
            fillDatabase()
        }
        while (true){
            println("=== Menu ===")
            println("1. Find a person\n" +
                    "2. Print all people\n" +
                    "0. Exit")
            when (getInput().toInt()) {
                1 -> findPerson()
                2 -> printAll()
                0 -> return
                else -> println("\nIncorrect option! Try again.\n")
            }
        }
    }

    private fun fillDatabase() {
        database.writeText("")
        println("Enter the number of people:")
        val lines = getInput().toInt()
        println("Enter all people:")
        for (i in 1..lines) {
            val line = getInput()
            database.appendText(line+"\n")
        }
        print("\n")
        readIndex()
    }

    private fun findPerson() {
        println("Select a matching strategy: ALL, ANY, NONE")
        when (getInput()) {
            "ALL" -> findAll()
            "ANY" -> findAny()
            "NONE" -> findNone()
        }

    }

    private fun findAll() {
        val queries = getQueries("all")
        var indices = mutableListOf<Int>()
        for (id in queries.indices) {
            if (queries[id] in invertedIndex.keys){
                indices = if (id == 0) {
                    invertedIndex[queries[id]]!!
                } else {
                    var tempIds = invertedIndex[queries[id]]!!
                    indices.intersect(tempIds).toMutableList()
                }
            }
        }

        if (indices.isNotEmpty()){
            printPersons(indices)
        }
    }

    private fun findAny() {
        val queries = getQueries("any")
        val indices = mutableListOf<Int>()
        for (query in queries) {
            if (query in invertedIndex.keys) {
                for (index in invertedIndex[query]!!) {
                    if (indices.isEmpty()) {
                        indices.add(index)
                    } else{
                        if (index !in indices) {
                            indices.add(index)
                        }
                    }
                }
            }
        }

        if (indices.isNotEmpty()){
            printPersons(indices)
        }

    }

    private fun findNone() {
        val queries = getQueries("none")
        var indices = mutableListOf<Int>()
        val noneIds = mutableListOf<Int>()
        for (query in queries) {
            if (query in invertedIndex.keys) {
                for (index in invertedIndex[query]!!) {
                    if (indices.isEmpty()) {
                        indices.add(index)
                    } else{
                        if (index !in indices) {
                            indices.add(index)
                        }
                    }
                }
            }
        }

        for (id in lines.indices) {
            if (id !in indices) {
                noneIds.add(id)
            }
        }

        if (noneIds.isNotEmpty()){
            printPersons(noneIds)
        }
    }

    private fun printPersons(indices: MutableList<Int>){
        println("${indices.size} persons found:")
        for (idx in indices) {
            println(lines[idx])
        }
        println(" ")
    }

    private fun getQueries(type: String): List<String> {
        println("Enter a name or email to search $type suitable people.")
        return getInput().split(" ")
    }

    private fun printAll(){
        println("\n=== List of people ===")
        val lines = database.readLines()
        for (l in 0..lines.lastIndex) {
            val line = lines[l]
            println(line.toString().replace(",", " "))
        }
        println(" ")
    }

    private fun getInput(): String{
        return scanner.nextLine()
    }

    private fun readIndex() {
        lines = database.readLines()
        for (l in 0..lines.lastIndex) {
            val line = lines[l].split(' ')
            for (word in line) {
                var word = word.toLowerCase()
                if (word in invertedIndex.keys) {
                    var values = invertedIndex.getValue(word)
                    values.add(l)
                    invertedIndex[word] = values
                } else {
                    invertedIndex[word] = mutableListOf(l)
                }
            }
        }
    }
}
