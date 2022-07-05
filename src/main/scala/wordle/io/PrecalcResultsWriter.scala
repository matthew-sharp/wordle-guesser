package wordle.io

import cats.effect.{IO, Resource}

import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel.MapMode
import java.nio.file.Paths

object PrecalcResultsWriter {
  val dir = "wordle-pre-calc"

  def mappedByteBuffer(size: Int): Resource[IO, ByteBuffer] = {
    val path = Paths.get(dir, "results")
    val file = Resource.fromAutoCloseable(IO.blocking(new RandomAccessFile(path.toFile, "rw")))
    file.map(_.getChannel.map(MapMode.READ_WRITE, 0, size))
  }
}
