package wordle.io

import cats.effect.{IO, Resource}
import net.jpountz.lz4.{LZ4Factory, LZ4FrameInputStream}

import java.io.{BufferedInputStream, InputStream}
import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ArrayBuffer

object WordResultReader {
  def readResultCache: IO[Array[Byte]] = {
    def read(in: InputStream, outbuf: Array[Byte], offset: Int): IO[Unit] = for {
      amtRead <- IO.blocking(in.read(outbuf, offset, outbuf.length - offset))
      _ <- if (amtRead > -1) read(in, outbuf, offset + amtRead)
      else IO.unit
    } yield ()

    val decompSizeArray = new Array[Byte](4)
    val path = Paths.get("results.lz4")

    val fin = Resource.fromAutoCloseable(IO.blocking(Files.newInputStream(path)))
    fin.use(in => for {
      _ <- IO.blocking(in.read(decompSizeArray, 0, 4))
      decompSize = ByteBuffer.wrap(decompSizeArray).getInt
      decompressedBytes = new Array[Byte](decompSize)

      lzIn = Resource.fromAutoCloseable(IO.blocking(new LZ4FrameInputStream(in)))
      _ <- lzIn.use(lzIn => read(lzIn, decompressedBytes, 0))
    } yield decompressedBytes)
  }
}
