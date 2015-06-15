package com.effect;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptC;
import android.support.v8.renderscript.Type;

class ImageProcessing {

	private static ImageProcessing instance;
	protected RenderScript rs;

	protected Allocation mInAllocation;
	protected Allocation allocationReal;

	protected Allocation allocationIm;

	protected Allocation mOutAllocation;

	protected ScriptC mScript;
	protected Context context;

	protected Bitmap outBitmap;

	private ImageProcessing(Context context, RenderScript rs, Bitmap outBitmap) {

		this.context = context;
		this.rs = rs;
		this.outBitmap = outBitmap;

		this.mOutAllocation = Allocation.createFromBitmap(this.rs, outBitmap,
				Allocation.MipmapControl.MIPMAP_NONE, 1);

	}

	public static ImageProcessing getInstance(Context context, RenderScript rs,
			Bitmap outBimap) {
		if (instance == null)
			instance = new ImageProcessing(context, rs, outBimap);
		return instance;
	}

	public void process(Allocation mInAllocation) {

		this.mInAllocation = mInAllocation;
		float inputData[] = new float[] { 1.f, 2.f, 3.f, 4.f };
		int numElements = inputData.length / 2;
		// invertImage(rs);
		// this.mInAllocation = Allocation.createSized(rs, Element.F32_2(rs),
		// numElements);
		// mInAllocation.copyFrom(inputData);
		fftImage(rs);
		// hpImage(rs);
		ifftImage(rs);
		// blendImage(rs);

	}

	public void invertImage(RenderScript rs) {
		ScriptC_InvertFilter invertScript = new ScriptC_InvertFilter(rs,
				context.getResources(), R.raw.invertfilter);
		invertScript.set_gIn(mInAllocation);

		invertScript.set_gOut(mOutAllocation);
		invertScript.set_gScript(invertScript);

		invertScript.invoke_filter();

	}

	public void fftImage(RenderScript rs) {
		ScriptC_FFTV3 fftScript = new ScriptC_FFTV3(rs, context.getResources(),
				R.raw.fftv3);

		Type.Builder yuvOutTyp = new Type.Builder(rs, Element.RGBA_8888(rs));
		yuvOutTyp.setX(outBitmap.getWidth());
		yuvOutTyp.setY(outBitmap.getHeight());

		Allocation inImAlloc = Allocation.createTyped(this.rs,
				yuvOutTyp.create(), Allocation.MipmapControl.MIPMAP_NONE, 0);

		Allocation re1dAlloc = Allocation.createSized(rs, Element.F32_4(rs),
				outBitmap.getWidth());

		Allocation im1dAlloc = Allocation.createSized(rs, Element.F32_4(rs),
				outBitmap.getWidth());

		allocationReal = Allocation.createSized(rs, Element.F32_4(rs),
				outBitmap.getByteCount());

		allocationIm = Allocation.createSized(rs, Element.F32_4(rs),
				outBitmap.getByteCount());

		fftScript.set_gInRe(mOutAllocation);

		fftScript.set_gInIm(inImAlloc);

		fftScript.set_re1d(re1dAlloc);
		fftScript.set_im1d(im1dAlloc);

		fftScript.set_gOutRe(allocationReal);
		fftScript.set_gOutIm(allocationIm);
		fftScript.set_gScript(fftScript);

		fftScript.invoke_filter();

	}

	public void hpImage(RenderScript rs) {
		ScriptC_HPFilter hpFilterScript = new ScriptC_HPFilter(rs,
				context.getResources(), R.raw.hpfilter);

		hpFilterScript.set_gInRe(allocationReal);
		hpFilterScript.set_gInIm(allocationIm);
		hpFilterScript.set_gScript(hpFilterScript);

		hpFilterScript.invoke_filter();

	}

	public void ifftImage(RenderScript rs) {
		Allocation re1dAlloc = Allocation.createSized(rs, Element.F32_4(rs),
				outBitmap.getWidth());

		Allocation im1dAlloc = Allocation.createSized(rs, Element.F32_4(rs),
				outBitmap.getWidth());
		
		ScriptC_IFFTV3 ifftScript = new ScriptC_IFFTV3(rs,
				context.getResources(), R.raw.ifftv3);
		ifftScript.set_height(8);
		ifftScript.set_width(8);
		ifftScript.set_re1d(re1dAlloc);
		ifftScript.set_im1d(im1dAlloc);
		ifftScript.set_gInIm(allocationIm);
		ifftScript.set_gInRe(allocationReal);
		ifftScript.set_gOutRe(mOutAllocation);
		// fftScript.set_gOutIm(allocationIm);
		ifftScript.set_gScript(ifftScript);

		ifftScript.invoke_filter();

	}

	public void blendImage(RenderScript rs) {
		ScriptC_ImageBlender blendScript = new ScriptC_ImageBlender(rs,
				context.getResources(), R.raw.imageblender);

		blendScript.set_gIn1(mOutAllocation);
		blendScript.set_gIn2(mOutAllocation);
		blendScript.set_gBlendMode(4);
		blendScript.set_gScript(blendScript);

		blendScript.invoke_filter();

		mOutAllocation.copyTo(outBitmap);
	}

	public static void destroy() {
		instance = null;
	}

};
