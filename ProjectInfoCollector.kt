import java.io.File
import java.io.PrintWriter

fun collectProjectInfo(projectDir: File, outputFile: File) {
    val writer = PrintWriter(outputFile)

    writer.println("Анализ проекта: ${projectDir.name}")
    writer.println("=".repeat(80))

    fun findKotlinFiles(dir: File): List<File> {
        val kotlinFiles = mutableListOf<File>()

        dir.listFiles()?.forEach { file ->
            when {
                file.isDirectory && !file.name.startsWith(".") &&
                        file.name != "build" && file.name != ".gradle" -> {
                    kotlinFiles.addAll(findKotlinFiles(file))
                }
                file.isFile && file.extension == "kt" -> {
                    kotlinFiles.add(file)
                }
            }
        }

        return kotlinFiles
    }

    fun readGradleFiles() {
        // Реализация readGradleFiles
    }

    fun readAppGradleFiles() {
        // Реализация readAppGradleFiles
    }

    fun analyzeDependencies() {
        // Реализация analyzeDependencies
    }

    fun collectKotlinFiles() {
        writer.println("\n📝 KOTLIN ФАЙЛЫ (первые 20 строк каждого):")
        writer.println("=".repeat(80))

        val kotlinFiles = findKotlinFiles(projectDir)
        kotlinFiles.forEachIndexed { index, file ->
            writer.println("\n[${index + 1}] ${file.relativeTo(projectDir)}")
            writer.println("-".repeat(60))
            try {
                val lines = file.readLines(Charsets.UTF_8).take(20)
                lines.forEachIndexed { lineNum, line ->
                    writer.println("${(lineNum + 1).toString().padStart(3)}: $line")
                }
                if (file.readLines().size > 20) {
                    writer.println("... (еще ${file.readLines().size - 20} строк)")
                }
            } catch (e: Exception) {
                writer.println("Ошибка чтения файла")
            }
            writer.println()
        }
    }

    readGradleFiles()
    readAppGradleFiles()
    analyzeDependencies()
    collectKotlinFiles()

    writer.println("\n" + "=".repeat(80))
    writer.println("КОНЕЦ ОТЧЕТА")
    writer.println("=".repeat(80))

    writer.close()
}

fun main() {
    val projectDir = File(System.getProperty("user.dir"))
    val outputFile = File("project_analysis_${System.currentTimeMillis()}.txt")

    println("🔍 Сбор информации о проекте...")
    println("📁 Директория: ${projectDir.absolutePath}")

    collectProjectInfo(projectDir, outputFile)

    println("✅ Отчет сохранен в: ${outputFile.absolutePath}")
    println("📊 Размер файла: ${outputFile.length()} байт")

    println("\n📋 Краткое содержимое отчета:")
    println("-".repeat(40))
    val previewLines = outputFile.readLines().take(10)
    previewLines.forEach { println(it) }
    println("...")
}