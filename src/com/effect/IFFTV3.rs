#pragma version(1)
#pragma rs java_package_name(com.effect)
// set from the java SDK level

rs_allocation gInRe;
rs_allocation gInIm;
rs_allocation gOutRe;
rs_allocation gOutIm;
rs_script gScript;

float gMixture = 1.0f;
//float pi = 1.31415485;

int width;
int height;

rs_allocation re1d;
rs_allocation im1d;

void custom_fft(uint32_t x, uint32_t y, int is_row) {
	uint32_t i, j, k, n1, n2, a;
	float c, s, e;

	float4 t1, t2;

	// Bit-reverse
	j = 0;

	uint m = (uint)(log((float) width) / log((float) 2));

	n2 = width / 2;
	for (i = 1; i < width - 1; i++) {
		n1 = n2;

		while (j >= n1) {
			j = j - n1;
			n1 = n1 / 2;
		}

		j = j + n1;

		if (i < j) {

			t1 = rsGetElementAt_float4(re1d, i);

			rsSetElementAt_float4(re1d, rsGetElementAt_float4(re1d, j), i);
			rsSetElementAt_float4(re1d, t1, j);

			t1 = rsGetElementAt_float4(im1d, i);

			rsSetElementAt_float4(im1d, rsGetElementAt_float4(im1d, j), i);
			rsSetElementAt_float4(im1d, t1, j);

		}

	}

	// FFT
	n1 = 0;
	n2 = 1;

	for (i = 0; i < m; i++) {
		n1 = n2;
		n2 = n2 + n2;
		a = 0;

		for (j = 0; j < n1; j++) {

			c = cos(-2 * M_PI * a / width);
			s = sin(-2 * M_PI * a / width);
			a += 1 << (m - i - 1);

			for (k = j; k < width; k = k + n2) {

				float4 r = rsGetElementAt_float4(re1d, k + n1);
				float4 im = rsGetElementAt_float4(im1d, k + n1);
				t1 = c * r - s * im;

				t2 = s * r + c * im;

				rsSetElementAt_float4(re1d, rsGetElementAt_float4(re1d, k) - t1,
						k + n1);
				rsSetElementAt_float4(im1d, rsGetElementAt_float4(im1d, k) - t2,
						k + n1);

				rsSetElementAt_float4(re1d, rsGetElementAt_float4(re1d, k) + t1,
						k);
				rsSetElementAt_float4(im1d, rsGetElementAt_float4(im1d, k) + t2,
						k);

			}
		}
	}

	for (int n = 0; n < width; n++) {

		float4 re = rsGetElementAt_float4(re1d, n);

		float4 im = rsGetElementAt_float4(im1d, n);

		if (is_row == 1) {

			rsSetElementAt_float4(gInRe, re, (x * width) + n);
			rsSetElementAt_float4(gInIm, im, (x * width) + n);

		} else {

			rsSetElementAt_float4(gOutRe, re, n, y);
//			rsSetElementAt_float4(gOutIm, im, y + (n * width));

		}

	}
}

static void setup() {

}

void filter() {
	setup();

	//row fft
//	width = rsAllocationGetDimX(gInRe);
//	height = rsAllocationGetDimY(gInRe);
	int is_row = 1;

	int *is_rowPtr = &is_row;

	struct rs_script_call restrict_for;
	restrict_for.strategy = RS_FOR_EACH_STRATEGY_DONT_CARE;
	restrict_for.xStart = 0;
	restrict_for.xEnd = width;
	restrict_for.yStart = 0;
	restrict_for.yEnd = 1;
	restrict_for.zStart = 0;
	restrict_for.zEnd = 0;
	restrict_for.arrayStart = 0;
	restrict_for.arrayEnd = width;

	rsForEach(gScript, gOutRe, gOutRe, &is_row, 0, &restrict_for);

	restrict_for.xStart = 0;
	restrict_for.xEnd = 1;
	restrict_for.yStart = 0;
	restrict_for.yEnd = height;

	is_row = 0;
	rsForEach(gScript, gOutRe, gOutRe, &is_row, 0, &restrict_for);

}

void root(const uchar4 *v_in_re, uchar4 *v_in_im, const int *usrData,
		uint32_t x, uint32_t y) {

	int is_row = (int) *usrData;

	if (is_row == 1) {

		for (int i = 0; i < width; i++) {

			float4 color = rsGetElementAt_float4(gInRe, (x * width) + i);
			float4 imcolor = rsGetElementAt_float4(gInIm, (x * width) + i);

			rsSetElementAt_float4(re1d, color, i);

			rsSetElementAt_float4(im1d, imcolor, i);
		}

		custom_fft(x, y, is_row);

	} else {

		for (int i = 0; i < height; i++) {

			float4 color = rsGetElementAt_float4(gInRe, y + (i * width));
			float4 imcolor = rsGetElementAt_float4(gInIm, y + (i * width));

			rsSetElementAt_float4(re1d, color, i);

			rsSetElementAt_float4(im1d, imcolor, i);

		}

		custom_fft(x, y, is_row);
		rsDebug("ifft", x);
	}

}

