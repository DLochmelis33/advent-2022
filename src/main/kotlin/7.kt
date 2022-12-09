import java.util.*

sealed class FileTreeNode(val name: String) {
    abstract val size: Long
}

class FileTreeDir(name: String, val children: MutableList<FileTreeNode>) : FileTreeNode(name) {
    override val size get() = children.sumOf { it.size }

    override fun toString() = "dir $name [$children]"
}

class FileTreeFile(name: String, override val size: Long) : FileTreeNode(name) {
    override fun toString() = "file $name $size"
}

fun FileTreeNode.walk(): Sequence<FileTreeNode> = when (this) {
    is FileTreeFile -> sequenceOf(this)
    is FileTreeDir -> sequenceOf(this) + children.asSequence().flatMap { it.walk() }
}

val fileTree = FileTreeDir("/", mutableListOf())
val walkStack = Stack<FileTreeNode>().apply { add(fileTree) }
val curNode: FileTreeNode
    get() = walkStack.peek()

fun main() {
    var s = readln()
    while (true) {
        if (s.startsWith("$")) {
            when (s.split(" ")[1]) {
                "cd" -> {
                    val nextDirName = s.split(" ")[2]
                    if (nextDirName == "..") {
                        walkStack.pop()
                    } else {
                        val nextNode = (curNode as FileTreeDir).children.first { it.name == nextDirName }
                        walkStack.add(nextNode)
                    }
                    s = readln()
                }
                "ls" -> {
                    inner@ while (true) {
                        s = readln()
                        if (!s.startsWith("$")) {
                            val (info1, name) = s.split(" ")
                            (curNode as FileTreeDir).children.add(
                                when (info1) {
                                    "dir" -> FileTreeDir(name, mutableListOf())
                                    else -> FileTreeFile(name, info1.toLong())
                                }
                            )
                        } else break@inner
                    }
                }
                "hehe" -> break
                else -> error("unrecognized command '$s'")
            }
        }
    }
    val maxAllowedSize = 40000000L
    val needToDeleteSize = fileTree.size - maxAllowedSize
    println(
        fileTree
            .walk()
            .filter { it is FileTreeDir && it.size > needToDeleteSize }
            .minBy { it.size }
            .size
    )
}