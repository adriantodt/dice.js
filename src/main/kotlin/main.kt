import com.github.adriantodt.dicenotation.evaluator.DiceEvaluator
import com.github.adriantodt.dicenotation.evaluator.DiceSolver
import com.github.adriantodt.dicenotation.lexer
import com.github.adriantodt.dicenotation.parser
import com.github.adriantodt.tartar.api.lexer.Source
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.appendElement
import kotlin.math.*
import kotlin.random.Random

fun main() {
    window.addEventListener("load", EventListener { onLoad() })
}

private val evaluator = DiceEvaluator()
    .value("pi", PI)
    .value("e", E)
    .function("log10") { log10(it[0].toDouble()) }
    .function("log2") { log2(it[0].toDouble()) }
    .function("ln") { ln(it[0].toDouble()) }
    .function("sin") { sin(it[0].toDouble()) }
    .function("cos") { cos(it[0].toDouble()) }
    .function("tan") { tan(it[0].toDouble()) }
    .function("average") { sequenceOf(*it).map(Number::toDouble).average() }
    .function("any") { it.random() }
    .function("int") { it[0].toInt() }
    .function("double") { it[0].toDouble() }
    .function("abs") { it[0].let { n -> if (n is Int) abs(n.toInt()) else abs(n.toDouble()) } }
    .functionAlias("average", "avg")
    .functionAlias("sin", "sen")
    .functionAlias("int", "integer", "long", "round")
    .functionAlias("double", "float", "decimal")

private val solver = DiceSolver { sides ->
    ((Random.nextDouble() * 0.9 + Random.nextDouble() * Random.nextDouble() * Random.nextDouble() * 0.9) * sides)
        .toInt().coerceIn(0, sides - 1).plus(1)
}

fun onLoad() {
    val input = document.getElementById("diceInput") as HTMLInputElement
    val button = document.getElementById("diceButton") as HTMLButtonElement
    val output = document.getElementById("diceOutput") as HTMLDivElement

    fun doDiceNotation() {
        try {
            val results = parser.parse(Source(input.value, "input"), lexer)
                .map { it.accept(solver) }
                .map { "${it.accept(evaluator)} ⟵ $it" }
            output.textContent = ""
            results.forEach { output.appendElement("p") { innerHTML = it } }
        } catch (e: Exception) {
            output.textContent = "Error: ${e.message}"
        }
    }

    input.addEventListener("keyup", { if ((it as KeyboardEvent).key == "Enter") doDiceNotation() })
    button.onclick = { doDiceNotation() }
}
