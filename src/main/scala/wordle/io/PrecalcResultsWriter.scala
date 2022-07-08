package wordle.io

import cats.effect.{IO, Resource}
import net.jpountz.lz4.LZ4FrameOutputStream.BLOCKSIZE
import net.jpountz.lz4.LZ4FrameOutputStream.FLG.Bits
import net.jpountz.lz4.{LZ4Factory, LZ4FrameOutputStream}
import net.jpountz.xxhash.XXHashFactory

import java.io.{BufferedOutputStream, ByteArrayInputStream, OutputStream}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel.MapMode
import java.nio.file.{Files, Paths}

object PrecalcResultsWriter {
  def compressWriteBytes(bytes: Array[Byte]): IO[Unit] = {
    val outpath = Paths.get("results")
    val decompressedLength = bytes.length

    val lenBytes = ByteBuffer.allocate(4).putInt(decompressedLength).array()

    val out = Resource.fromAutoCloseable(IO.blocking(Files.newOutputStream(outpath)))
    out.use(
      out => for {
        _ <- IO.blocking(out.write(lenBytes))
        lzOut = Resource.fromAutoCloseable(IO.blocking(lz4OutStream(out, decompressedLength)))
        _ <- lzOut.use(lzOut => IO.blocking(lzOut.write(bytes)))
      } yield ()
    )
  }

  private def lz4OutStream(out: OutputStream, decompressedLength: Int): LZ4FrameOutputStream = {
    val compressor = LZ4Factory.fastestInstance().highCompressor()
    new LZ4FrameOutputStream(
      out,
      BLOCKSIZE.SIZE_4MB,
      decompressedLength,
      compressor,
      XXHashFactory.fastestInstance().hash32(),
      Bits.BLOCK_INDEPENDENCE,
    )
  }
}
