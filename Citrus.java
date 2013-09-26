import processing.core.*;
import java.util.*;
import static java.lang.System.out;

// # Citrus
// A citrus shape containing:
//
// - epicarp: outermost part of the peel (the orange skin of an orange)
// - mesocarp: the white-ish inner part of the peel
// - endocarp: the juicy, yummy interior, made up of...
// - vesicles: the little segments containing juice
// - central column: the innermost white core (inedible)
//
// (thank you wikipedia)
public class Citrus {
	private PApplet app;
	public float radius;
	private int vesicleCount;
	private float epicarpWidth;
	private float mesocarpWidth;
	private float centerWidth;
	public float rotation;
	int baseColor;

	public enum COLOR_CHANNEL {
		R,
		G,
		B;
	}

	public PVector position;
	public PVector velocity;

	private float spin;
	private float totalSpin;


	// ## Citrus
	//
	// - app: a reference to the processing app (used for drawing)
	// - radius: radius of the entire fruit
	// - vesicleCount: the number of juicy segments in the fruit
	// - epicarpWidth: the thickness of the outer portion of the peel
	// - mesocarpWidth: the thickness of the inner portion of the peel
	// - centerWidth: the thickness of the central column
	// - baseColor: the base color of the fruit
	public Citrus(PApplet app, float radius, int vesicleCount, float epicarpWidth, float mesocarpWidth, float centerWidth, int baseColor) {
		this.app = app;
		this.radius = radius;
		this.vesicleCount = vesicleCount;
		this.epicarpWidth = epicarpWidth;
		this.mesocarpWidth = mesocarpWidth;
		this.centerWidth = centerWidth;
		this.baseColor = baseColor;
		this.position = new PVector(0f, 0f);
		this.velocity = new PVector(0f, 0f);
		this.rotation = 0f;
		this.spin = 0f;
	}

	// ## draw
	// Draws the citrus using the current app at the specified position
	public void draw(PVector pos) {
		// determine lighter shades of the base color
		int fillColor1 = lightenColor(baseColor, 40);
		int fillColor2 = lightenColor(baseColor, 80);

		// draw epicarp
		app.smooth();
		app.strokeWeight(epicarpWidth);
		app.stroke(baseColor);
		app.fill(fillColor1);
		app.ellipse(pos.x, pos.y, radius * 2f, radius * 2f);

		// draw internal endocarp bounding circle
		float endoRadius = radius - mesocarpWidth;

		app.strokeWeight(1f);
		app.fill(fillColor2);
		app.ellipse(pos.x, pos.y, endoRadius * 2f, endoRadius * 2f);

		// draw vesicles
		// split into even sections
		// draw the line from a point on the circumference of the endocarp (vesicles section)
		// to the the circumference of the center column
		float step = app.TWO_PI / vesicleCount;
		float x;
		float y;
		float x2;
		float y2;
		float angle;

		for (int i = 0; i <= vesicleCount; i++) {
			angle = (i * step) + ((rotation / 180f) * app.PI) + spin;
			x = app.cos(angle) * endoRadius;
			y = app.sin(angle) * endoRadius;
			x2 = app.cos(angle) * (centerWidth);
			y2 = app.sin(angle) * (centerWidth);

			app.line(pos.x + x, pos.y + y,
					pos.x + x2, pos.y + y2);
		}

		app.fill(255);
		app.ellipse(pos.x, pos.y, centerWidth * 2f, centerWidth * 2f);
	}

	// ## draw()
	// Draws the citrus at its current position
	public void draw() {
		draw(position);
	}

	// ## physicsUpdate
	//
	// - Updates position using velocity
	// - Decays velocity over time
	public void physicsUpdate() {
		position = PVector.add(position, velocity);

		velocity = new PVector(
			app.lerp(velocity.x, 0f, .1f),
			app.lerp(velocity.y, 0f, .1f)
		);

		spin = app.lerp(spin, totalSpin, .1f);
	}

	// ## addSpin
	// Adds spin based upon impact force
	public void addSpin(PVector force) {
		totalSpin += (force.mag() / 10f) * app.PI * (force.x >= 0 ? 1f : -1f);
	}

	// Color Set Functions
	// -------------------
	private int setAlpha(int c, int a) {
		return (c & 0x00FFFFFF) | (a << 24);
	}

	private int setRed(int c, int r) {
		return (c & 0xFF00FFFF) | (r << 16);
	}

	private int setGreen(int c, int g) {
		return (c & 0xFFFFFF00) | (g << 8);
	}

	private int setBlue(int c, int b) {
		return (c & 0xFFFFFF00) | b;
	}

	// ## getDominantColor(int color)
	// Returns the dominant color channel (by highest value)
	private COLOR_CHANNEL getDominantColor(int c) {
		int r = (c >> 16) & 0xFF;
		int g = (c >> 8) & 0xFF;
		int b = c & 0xFF;

		if (r >= g && r >= b) {
			return COLOR_CHANNEL.R;
		}

		if (g >= r && b >= b) {
			return COLOR_CHANNEL.G;
		}

		if (b >= g && b >= r) {
			return COLOR_CHANNEL.B;
		}

		return COLOR_CHANNEL.R;
	}

	// ## darkenColor
	// Returns a copy of the specified color,
	// darkened by the specified amount (lower limit of 0).
	private int darkenColor(int c, int amt) {
		COLOR_CHANNEL channel = getDominantColor(c);
		int alpha = (c >> 24) & 0xFF;
		int r = (c >> 16) & 0xFF;
		int g = (c >> 8) & 0xFF;
		int b = c & 0xFF;

		switch (channel) {
			case R:
				g = max(g - amt, 255);
				b = max(b - amt, 255);
				break;
			case G:
				r = max(r - amt, 255);
				b = max(b - amt, 255);
				break;
			case B:
				r = max(r - amt, 255);
				g = max(g - amt, 255);
				break;
		}

		// rebuild the color
		return (
			(alpha << 24) |
			(r << 16) |
			(g << 8) |
			b
		);
	}

	// ## lightenColor
	// Returns a copy of the specified color,
	// lightened by the specified amount (capped at 255).
	private int lightenColor(int c, int amt) {
		COLOR_CHANNEL channel = getDominantColor(c);
		int alpha = (c >> 24) & 0xFF;
		int r = (c >> 16) & 0xFF;
		int g = (c >> 8) & 0xFF;
		int b = c & 0xFF;

		//out.println(channel.toString());

		switch (channel) {
			case R:
				g = min(g + amt, 255);
				b = min(b + amt, 255);
				break;
			case G:
				r = min(r + amt, 255);
				b = min(b + amt, 255);
				break;
			case B:
				r = min(r + amt, 255);
				g = min(g + amt, 255);
				break;
		}

		// rebuild the color
		return (
			(alpha << 24) |
			(r << 16) |
			(g << 8) |
			b
		);
	}

	// ## min
	// Returns the lower of two values
	private int min(int a, int b) {
		if (a <= b) {
			return a;
		}

		return b;
	}

	// ## max
	// Returns the higher of two values
	private int max(int a, int b) {
		if (a >= b) {
			return a;
		}

		return b;
	}
}
