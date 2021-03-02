package nl.mvdb.heroku.oauth.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HerokuApiWithOAuthApplication

fun main(args: Array<String>) {
	runApplication<HerokuApiWithOAuthApplication>(*args)
}
