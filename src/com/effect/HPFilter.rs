#pragma version(1)
#pragma rs java_package_name(com.effect)
// set from the java SDK level

rs_allocation gInRe;
rs_allocation gInIm;
rs_allocation gOut;
rs_script gScript;

float gMixture = 1.0f;


static void setup() {

}

void filter() {
	setup();
	rsForEach(gScript, gInRe, gInIm, 0, 0); // for each element of the input allocation,
										  // call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x,
		uint32_t y) {

	uint32_t D0;
	uint32_t M;
	uint32_t N;

	float4 re = rsUnpackColor8888(
			*(const uchar4*) rsGetElementAt(gInRe, x, y));

	float4 im = rsUnpackColor8888(
			*(const uchar4*) rsGetElementAt(gInIm, x, y));

	uint32_t a = N / 2;
	uint32_t b = M / 2;

	float distance = 0;
	float H = 0;
	float w = 0;
	float v = 0;

	if (x == 0 && y <= a)
		distance = y;
	else if (x == 0 && y > a)
		distance = a - (y - a);
	else if (y == 0 && x <= b)
		distance = x;
	else if (y == 0 && x > b)
		distance = b - (x - b);
	else {
		if (y <= a)
			v = y;
		else if (y > a)
			v = a - (y - a);
		else if (x <= b)
			w = x;
		else if (x > b)
			w = b - (x - b);
		//	                   w = distance(x,0);
		//	                   x = distance(0,y);
		distance = sqrt(w * w + v * v);
	}

	H = 1 - exp(-(distance * distance) / (2 * (D0 * D0)));

	re.r = re.r * H;
	re.g = re.g * H;
	re.b = re.b * H;

	im.r = im.r * H;
	im.g = im.g * H;
	im.b = im.b * H;

//	theF4.a = 0.4f;

//	float4 f3;
//	f3 = blend_overlay(f4, theF4, gMixture, 1);

//	*v_out = rsPackColorTo8888(f3);
}
