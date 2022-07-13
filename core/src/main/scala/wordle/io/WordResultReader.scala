package wordle.io

import cats.effect.{IO, Resource}
import net.jpountz.lz4.{LZ4Factory, LZ4FrameInputStream}

import java.io.InputStream
import java.nio.file.{Files, Paths}
import scala.collection.immutable.ArraySeq

object WordResultReader {
  def readResultCache: IO[IArray[Byte]] = {
    def read(in: InputStream, outbuf: Array[Byte], offset: Int): IO[Unit] = for {
      amtRead <- IO.blocking(in.read(outbuf, offset, outbuf.length - offset))
      _ <- if (amtRead > -1) read(in, outbuf, offset + amtRead)
      else IO.unit
    } yield ()

    val path = Paths.get("results.lz4")

    val lzIn = for {
      fin <- Resource.fromAutoCloseable(IO.blocking(Files.newInputStream(path)))
      lzIn <- Resource.fromAutoCloseable(IO.blocking(new LZ4FrameInputStream(fin, true)))
    } yield lzIn
    val bytes = lzIn.use(lzIn =>
      val decompSize = lzIn.getExpectedContentSize.toInt
      val decompressedBytes = new Array[Byte](decompSize)
        read (lzIn, decompressedBytes, 0).as(decompressedBytes)
    )
    bytes.map(IArray.unsafeFromArray)
  }
}
