#pragma version(1)
#pragma rs java_package_name(com.effect)
// set from the java SDK level

rs_allocation gInRe;
rs_allocation gInIm;
rs_allocation gOutRe;
rs_script gScript;
int width;
int height;
int d0;


static void setup() {

}

void filter() {
	setup();


	rsForEach(gScript, gOutRe, gOutRe, 0, 0); // for each element of the input allocation,
											// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x,
		uint32_t y) {
	int i;
	int j;



	if(x>0)
		i = x*width+y;
	else
		i = x+y;

	j = y;


	float4 re = rsGetElementAt_float4(gInRe, i);
	float4 im = rsGetElementAt_float4(gInIm, i);



	uint32_t a = height / 2;
	uint32_t b = width / 2;



	float distance = 0;
	float H = 0;
	float w = 0;
	float v = 0;

	if (x == 0 && y < a)
		distance = y;
	else if (x == 0 && y >= a)
		distance = 1+a - (y+1 - a);
	else if (y == 0 && x< b)
		distance = x;
	else if (y == 0 && x >= b)
		distance = 1+b - (x+1 - b);
	else if(x>0 && y>0) {
		if (y < a)
			v = y;
		else if (y >= a)
			v = a - (y - a);
		if (x < b)
			w = x;
		else if (x >= b)
			w = b - (x - b);

		distance = sqrt(w * w + v * v);


	}

	H = 1 - exp(-(distance * distance) / (2 * (d0 * d0)));

	re.r = re.r * H;
	re.g = re.g * H;
	re.b = re.b * H;

	im.r = im.r * H;
	im.g = im.g * H;
	im.b = im.b * H;



	rsSetElementAt_float4(gInRe, re, i);
	rsSetElementAt_float4(gInIm, im, i);

}
