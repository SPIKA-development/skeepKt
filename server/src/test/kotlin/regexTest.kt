import network.usernameRegex

fun main() {
    println(usernameRegex.matchEntire("h"))
    println(usernameRegex.matchEntire("안"))
    println(usernameRegex.matchEntire("!"))
}