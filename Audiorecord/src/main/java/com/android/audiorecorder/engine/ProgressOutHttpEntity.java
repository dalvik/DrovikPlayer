package com.android.audiorecorder.engine;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProgressOutHttpEntity extends HttpEntityWrapper {

	private final ProgressListener listener;

	public ProgressOutHttpEntity(final HttpEntity entity,
			final ProgressListener listener) {
		super(entity);
		this.listener = listener;
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;

		CountingOutputStream(final OutputStream out,
				final ProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}

		@Override
		public void write(final byte[] b, final int off, final int len)
				throws IOException {
			// NO, double-counting, as super.write(byte[], int, int)
			// delegates to write(int).
			// super.write(b, off, len);
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred);
		}

		@Override
		public void write(final int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}

	}

	@Override
	public void writeTo(final OutputStream out) throws IOException {
		this.wrappedEntity.writeTo(out instanceof CountingOutputStream ? out
				: new CountingOutputStream(out, this.listener));
	}

	public interface ProgressListener {
		public void transferred(long transferedBytes);
	}

	public interface UploadResult {

		public final static int IDLE = -1;
		public final static int PROCESS = 0;
		public final static int SUCCESS = 1;
		public final static int FAIL = 2;

		public void onResult(int result, int id, long progress);
	}
}
