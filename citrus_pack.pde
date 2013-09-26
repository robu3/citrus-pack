import java.util.*;

// good settings:
// x,y: 800,600
// count: 60
// min/max: 35/100
int screenX = 800; 
int screenY = 600;
boolean redraw = false;

// citrus paramaters
int citrusCount = 60;
int[] colors = {
	color(255, 128, 0),
	color(50, 217, 50),
	color(255, 100, 21), // grapefruit
	//color(223, 235, 0)
	color(231, 250, 62) // yellow
};

float minRadius = 35f;
float maxRadius = 100f;

int minVesicleCount = 8;
int maxVesicleCount = 14;

// used to take screenshots at an interval
int counter = 0;

Citrus[] fruits = new Citrus[citrusCount];


void setup() {
	size(screenX, screenY, P2D);
	redraw = true;

	for (int i = 0; i < citrusCount; i++) {
		float r = random(minRadius, maxRadius);
		Citrus c = new Citrus(this,
			r,
			int(random(minVesicleCount, maxVesicleCount)),
			r / (random(25f, 50f)),
			r / (random(3f, 5f)),
			r / (random(5f, 20f)),
			colors[int(random(0, colors.length))]
			//colors[2]
		);
		c.position = new PVector(random(0, screenX), random(0, screenY));
		c.rotation = random(0f, 360f);
		fruits[i] = c;
	}
}

void keyPressed() {
	// "s" key captures a screenshot
	if (key == 's' || key == 'S') {
		save("img_" + System.currentTimeMillis() + ".png");
	}
}

void draw() {
	physicsUpdate();
	if (redraw) {
		background(255);
		smooth();
		// draw the fruit
		for (Citrus c : fruits) {
			//c.draw(new PVector(screenX / 2, screenY / 2));
			if (c != null) {
				c.draw();
			}
		}
	}

	if (counter % 20 == 0) {
		// uncomment to capture screen shots
		//save("images/img_" + System.currentTimeMillis() + ".png");
	}
}

// ## physicsUpdate
// __Really__ primitive physics sim, limited to checking for collisions and apply force in
// opposing directions. Radius of the citrus fruits is considered to be directly related to
// mass.
void physicsUpdate() {

	for (int i = 0; i < fruits.length; i++) {
		Citrus c = fruits[i];
		if (c != null) {
			for (int j = 0; j < fruits.length; j++) {
				Citrus c2 = fruits[j];
				if (j == i || c2 == null) {
					continue;
				}

				if (doOverlap(c, c2)) {
					PVector velocity = new PVector(c.position.x, c.position.y);
					velocity.sub(c2.position);

					if (velocity.mag() < 1f) {
						velocity = new PVector(
							random(-1f, 1f),
							random(-1f, 1f)
						);
					} else {

						velocity.normalize();

						c.velocity = PVector.add(velocity, c.velocity);
						c.addSpin(velocity);
					}
				}
			}

			c.velocity.add(boundsVector(c));
			c.physicsUpdate();
		}
	}
}

// ## doOverlap
// Returns true if two citrus fruits overlap
public boolean doOverlap(Citrus a, Citrus b) {
	float d = a.position.dist(b.position);
	return (d < a.radius + b.radius);
}

// ## boundsVector
// Returns the vector required to push the citrus back into bounds
public PVector boundsVector(Citrus c) {
	PVector v = new PVector(0, 0);

	if (c.position.x > screenX) {
		v.x = screenX - c.position.x;
	} 
	else if (c.position.x < 0) {
		v.x = -c.position.x;
	}

	if (c.position.y > screenY) {
		v.y = screenX - c.position.y;
	} 
	else if (c.position.y < 0) {
		v.y = -c.position.y;
	}

	v.normalize();

	return v;
}
