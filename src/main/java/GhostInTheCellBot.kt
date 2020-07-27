import java.util.*

enum class EntityType {
    FACTORY,
    TROOP;
}

enum class Owner {
    ALLY,
    RIVAL,
    NEUTRAL;

    companion object {
        fun fromInt(input: Int): Owner {
            return when(input) {
                1 -> ALLY
                -1 -> RIVAL
                0 -> NEUTRAL
                else -> throw RuntimeException()
            }
        }
    }
}

data class Factory(val id:Int, var owner: Owner = Owner.NEUTRAL, var cyborgs: Int = 0, var production: Int = 0)

data class Troop(val id: Int, val owner: Owner, val from: Factory, val to: Factory, val cyborgs: Int, var remaining: Int)

data class Link(val first: Factory, val second: Factory, val distance: Int)

fun findTarget(rivalFactories: List<Factory>, neutralFactories: List<Factory>): Factory {
    val searchList = if(neutralFactories.isEmpty()) {
        rivalFactories
    } else {
        neutralFactories
    }
    var answer = searchList[0]
    for(factory in searchList) {
        if(factory.production > answer.production) {
            answer = factory
        } else if(factory.production == answer.production) {
            if(factory.cyborgs < answer.cyborgs) {
                answer = factory
            }
        }
    }
    return answer
}

fun findFrom(allyFactories: List<Factory>, links: List<Link>, target: Factory): Factory {
    var answer = allyFactories.find { it.cyborgs != 0 } ?: return allyFactories[0]
    var bestDistance = -1
    for(factory in allyFactories) {
        val link = links.find { (it.first.id == factory.id && it.second.id == target.id) || (it.first.id == target.id && it.second.id == factory.id) }
        if(link == null) continue
        if(bestDistance == -1 || answer.cyborgs == 0) {
            bestDistance = link.distance
            answer = factory
            continue
        }else if(factory.cyborgs < target.cyborgs) {
            continue
        }else if(link.distance < bestDistance || (link.distance == bestDistance && answer.cyborgs < factory.cyborgs)) {
            bestDistance = link.distance
            answer = factory
        }
        System.err.println("On step answer: $answer distance: $bestDistance")
    }
    return answer
}

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) {
    val links = mutableListOf<Link>()
    val factories = mutableListOf<Factory>()
    val input = Scanner(System.`in`)
    val factoryCount = input.nextInt() // the number of factories
    val linkCount = input.nextInt() // the number of links between factories
    for (i in 0 until linkCount) {
        val factory1 = input.nextInt()
        val factory2 = input.nextInt()
        val distance = input.nextInt()
        System.err.println("$factory1 $factory2 $distance")
        val firstFactory = Factory(factory1)
        val secondFactory = Factory(factory2)
        factories.add(firstFactory)
        factories.add(secondFactory)
        links.add(Link(firstFactory, secondFactory, factoryCount))
    }

    // game loop
    while (true) {
        val entityCount = input.nextInt() // the number of entities (e.g. factories and troops)
        val troops = mutableListOf<Troop>()
        val allyFactories = mutableListOf<Factory>()
        val rivalFactories = mutableListOf<Factory>()
        val neutralFactories = mutableListOf<Factory>()
        for (i in 0 until entityCount) {
            val entityId = input.nextInt()
            val entityType = input.next()
            val owner = input.nextInt()
            val arg2 = input.nextInt() //Factory: cyborgs, Troop: from
            val arg3 = input.nextInt() //Factory: production, Troop: to
            val arg4 = input.nextInt() //Troop: cyborgs
            val arg5 = input.nextInt() //Troop: remaining
            when(EntityType.valueOf(entityType)) {
                EntityType.FACTORY -> {
                    val factory = factories.find { it.id == entityId }!!
                    factory.owner = Owner.fromInt(owner)
                    factory.cyborgs = arg2
                    factory.production = arg3
                    when(factory.owner) {
                        Owner.ALLY -> {
                            allyFactories.add(factory)
                        }
                        Owner.RIVAL -> {
                            rivalFactories.add(factory)
                        }
                        Owner.NEUTRAL -> {
                            neutralFactories.add(factory)
                        }
                    }
                    System.err.println("$factory")
                }
                EntityType.TROOP -> {
                    val troop = Troop(entityId, Owner.fromInt(owner), factories.find { it.id == arg2 }!!, factories.find { it.id == arg3 }!!, arg4, arg5)
                    troops.add(troop)
                    System.err.println("$troop")
                }
            }

        }

        val target = findTarget(rivalFactories, neutralFactories)
        val from = findFrom(allyFactories, links, target)
        val send = if(target.cyborgs == 0) 1 else target.cyborgs

        println("MOVE ${from.id} ${target.id} ${send}")

        // Write an action using println()
        // To debug: System.err.println("Debug messages...");


        // Any valid action, such as "WAIT" or "MOVE source destination cyborgs"
        //println("WAIT") WE DO NOT WAIT
    }
}