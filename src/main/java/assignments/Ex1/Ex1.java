package assignments.Ex1;

/**
 * Introduction to Computer Science 2026, Ariel University,
 * Ex1: arrays, static functions and JUnit
 * https://docs.google.com/document/d/1GcNQht9rsVVSt153Y8pFPqXJVju56CY4/edit?usp=sharing&ouid=113711744349547563645&rtpof=true&sd=true
 *
 * This class represents a set of static methods on a polynomial functions - represented as an array of doubles.
 * The array {0.1, 0, -3, 0.2} represents the following polynomial function: 0.2x^3-3x^2+0.1
 * This is the main Class you should implement (see "add your code below")
 *
 * @author boaz.benmoshe

 */
public class Ex1 {
	/** Epsilon value for numerical computation, it serves as a "close enough" threshold. */
	public static final double EPS = 0.001; // the epsilon to be used for the root approximation.
	/** The zero polynomial function is represented as an array with a single (0) entry. */
	public static final double[] ZERO = {0};
	/**
	 * Computes the f(x) value of the polynomial function at x.
	 * @param poly - polynomial function
	 * @param x
	 * @return f(x) - the polynomial function value at x.
	 */
	public static double f(double[] poly, double x) {
		double ans = 0;
		for(int i=0;i<poly.length;i++) {
			double c = Math.pow(x, i);
			ans += c*poly[i];
		}
		return ans;
	}
	/** Given a polynomial function (p), a range [x1,x2] and an epsilon eps.
	 * This function computes an x value (x1<=x<=x2) for which |p(x)| < eps, 
	 * assuming p(x1)*p(x2) <= 0.
	 * This function should be implemented recursively.
	 * @param p - the polynomial function
	 * @param x1 - minimal value of the range
	 * @param x2 - maximal value of the range
	 * @param eps - epsilon (positive small value (often 10^-3, or 10^-6).
	 * @return an x value (x1<=x<=x2) for which |p(x)| < eps.
	 */


	 /**
     * Pseudocode:
     *  mid = (x1 + x2) / 2
     *  if |f(mid)| < eps:
     *      return mid
     *  if f(x1)*f(mid) <= 0:
     *      recurse on [x1, mid]
     *  else:
     *      recurse on [mid, x2]
     */
	public static double root_rec(double[] p, double x1, double x2, double eps) {
		double f1 = f(p,x1);
		double x12 = (x1+x2)/2;
		double f12 = f(p,x12);
		if (Math.abs(f12)<eps) {return x12;}
		if(f12*f1<=0) {return root_rec(p, x1, x12, eps);}
		else {return root_rec(p, x12, x2, eps);}
	}
	/**
	 * This function computes a polynomial representation from a set of 2D points on the polynom.
	 * The solution is based on: //	http://stackoverflow.com/questions/717762/how-to-calculate-the-vertex-of-a-parabola-given-three-points
	 * Note: this function only works for a set of points containing up to 3 points, else returns null.
	 * @param xx
	 * @param yy
	 * @return an array of doubles representing the coefficients of the polynom.
	 */

	/** Pseudocode:
     *  if 2 points:
     *      compute slope a and intercept b
     *  if 3 points:
     *      compute quadratic coefficients a, b, c
     *  else:
     *      return null
     */
	public static double[] PolynomFromPoints(double[] xx, double[] yy) {
		if (xx == null || yy == null || xx.length != yy.length || xx.length < 2 || xx.length > 3) {
			return null;
		}

		int n = xx.length;
		double[] ans;

		if (n == 2) {
			double x0 = xx[0], x1 = xx[1];
			double y0 = yy[0], y1 = yy[1];

			if (Double.compare(x0, x1) == 0) return null;

			double a = (y1 - y0) / (x1 - x0);
			double b = y0 - a * x0;

			ans = new double[]{a, b};

		} else {
			double x0 = xx[0], x1 = xx[1], x2 = xx[2];
			double y0 = yy[0], y1 = yy[1], y2 = yy[2];

			double denom = (x0 - x1)*(x0 - x2)*(x1 - x2);
			if (denom == 0) return null; 

			double a = (x2 * (y1 - y0) + x1 * (y0 - y2) + x0 * (y2 - y1)) / denom;
			double b = (x2 * x2 * (y0 - y1) + x1 * x1 * (y2 - y0) + x0 * x0 * (y1 - y2)) / denom;
			double c = (x1 * x2 * (x1 - x2) * y0 + x2 * x0 * (x2 - x0) * y1 + x0 * x1 * (x0 - x1) * y2) / denom;

			ans = new double[]{a, b, c};
		}

		return ans;
	}

	/** Two polynomials functions are equal if and only if they have the same values f(x) for n+1 values of x,
	 * where n is the max degree (over p1, p2) - up to an epsilon (aka EPS) value.
	 * @param p1 first polynomial function
	 * @param p2 second polynomial function
	 * @return true iff p1 represents the same polynomial function as p2.
	 */

	/* Pseudocode:
     *  for x from 0 to max degree:
     *      compute f1(x) and f2(x)
     *      if |f1 - f2| > EPS:
     *          return false
     *  return true
     */
	public static boolean equals(double[] p1, double[] p2) {
		int n = Math.max(p1.length, p2.length) - 1; 

		double EPS = Ex1.EPS; // use the same EPS as tests
		for (int x = 0; x <= n; x++) {
			double y1 = 0;
			double y2 = 0;
			for (int i = 0; i < p1.length; i++) {
				y1 += p1[i] * Math.pow(x, i);
			}
			for (int i = 0; i < p2.length; i++) {
				y2 += p2[i] * Math.pow(x, i);
			}
			if (Math.abs(y1 - y2) > EPS) {
				return false; 
			}
		}
		return true; 
	}


	/** 
	 * Computes a String representing the polynomial function.
	 * For example the array {2,0,3.1,-1.2} will be presented as the following String  "-1.2x^3 +3.1x^2 +2.0"
	 * @param poly the polynomial function represented as an array of doubles
	 * @return String representing the polynomial function:
	 */

		
	/* Pseudocode:
	*  for each term from high degree to low:
	*      skip zero
	*      append sign and coefficient
	*      append x^power if needed
	*  return string
	*/
	public static String poly(double[] poly) {
		if (poly == null || poly.length == 0) return "0";

		StringBuilder sb = new StringBuilder();
		int n = poly.length - 1; 
		boolean firstTerm = true;

		for (int i = n; i >= 0; i--) {
			double coef = poly[i];
			if (Math.abs(coef) < 1e-9) continue;

			if (coef > 0 && !firstTerm) {
				sb.append(" +");
			} else if (coef < 0) {
				sb.append(firstTerm ? "-" : " -");
			}

			double absCoef = Math.abs(coef);

			if (!(absCoef == 1 && i != 0)) {
				sb.append(absCoef);
			}

			if (i >= 1) {
				sb.append("x");
				if (i > 1) sb.append("^").append(i);
			}

			firstTerm = false;
		}
		if (sb.length() == 0) return "0";

		return sb.toString();
	}

	/**
	 * Given two polynomial functions (p1,p2), a range [x1,x2] and an epsilon eps. This function computes an x value (x1<=x<=x2)
	 * for which |p1(x) -p2(x)| < eps, assuming (p1(x1)-p2(x1)) * (p1(x2)-p2(x2)) <= 0.
	 * @param p1 - first polynomial function
	 * @param p2 - second polynomial function
	 * @param x1 - minimal value of the range
	 * @param x2 - maximal value of the range
	 * @param eps - epsilon (positive small value (often 10^-3, or 10^-6).
	 * @return an x value (x1<=x<=x2) for which |p1(x) - p2(x)| < eps.
	 */

	/* Pseudocode:
     *  use binary search:
     *      mid = (x1 + x2)/2
     *      compute f = p1(mid) - p2(mid)
     *      if |f| < eps: return mid
     *      else recurse on half with sign change
     */
	public static double sameValue(double[] p1, double[] p2, double x1, double x2, double eps) {
		double f1 = f(p1, x1) - f(p2, x1);
		double f2 = f(p1, x2) - f(p2, x2);

		if (Math.abs(f1) < eps) return x1;
		if (Math.abs(f2) < eps) return x2;

		double mid = 0;

		while ((x2 - x1) > eps) {
			mid = (x1 + x2) / 2;
			double fm = f(p1, mid) - f(p2, mid);

			if (Math.abs(fm) < eps) return mid;

			if (f1 * fm <= 0) {
				x2 = mid;
				f2 = fm;
			} else {
				x1 = mid;
				f1 = fm;
			}
		}

		return (x1 + x2) / 2;
	}

	/**
	 * Given a polynomial function (p), a range [x1,x2] and an integer with the number (n) of sample points.
	 * This function computes an approximation of the length of the function between f(x1) and f(x2) 
	 * using n inner sample points and computing the segment-path between them.
	 * assuming x1 < x2. 
	 * This function should be implemented iteratively (none recursive).
	 * @param p - the polynomial function
	 * @param x1 - minimal value of the range
	 * @param x2 - maximal value of the range
	 * @param numberOfSegments - (A positive integer value (1,2,...).
	 * @return the length approximation of the function between f(x1) and f(x2).
	 */

	/* Pseudocode:
     *  divide [x1,x2] into n segments
     *  sum sqrt((dx)^2 + (dy)^2) for each segment
     */
	public static double length(double[] p, double x1, double x2, int numberOfSegments) {
		if (p == null || numberOfSegments <= 0) return 0.0;
		if (x1 == x2) return 0.0;

		if (x2 < x1) {
			double tmp = x1; x1 = x2; x2 = tmp;
		}

		double dx = (x2 - x1) / numberOfSegments;
		double prevX = x1;
		double prevY = f(p, prevX);
		double total = 0.0;

		for (int i = 1; i <= numberOfSegments; i++) {
			double curX = x1 + i * dx;
			double curY = f(p, curX);
			total += Math.hypot(curX - prevX, curY - prevY);
			prevX = curX;
			prevY = curY;
		}

		return total;
	}

	
	/**
	 * Given two polynomial functions (p1,p2), a range [x1,x2] and an integer representing the number of Trapezoids between the functions (number of samples in on each polynom).
	 * This function computes an approximation of the area between the polynomial functions within the x-range.
	 * The area is computed using Riemann's like integral (https://en.wikipedia.org/wiki/Riemann_integral)
	 * @param p1 - first polynomial function
	 * @param p2 - second polynomial function
	 * @param x1 - minimal value of the range
	 * @param x2 - maximal value of the range
	 * @param numberOfTrapezoid - a natural number representing the number of Trapezoids between x1 and x2.
	 * @return the approximated area between the two polynomial functions within the [x1,x2] range.
	 */

	/* Pseudocode:
     *  find all points where p1 - p2 changes sign
     *  split interval at these roots
     *  sum trapezoid areas in each interval
     */
	public static double area(double[] p1, double[] p2, double x1, double x2, int numberOfTrapezoid) {
		if (p1 == null || p2 == null) throw new IllegalArgumentException("Null polynomial.");
		if (p1.length == 0 || p2.length == 0) throw new IllegalArgumentException("Empty polynomial.");
		if (Double.isNaN(x1) || Double.isNaN(x2)) throw new IllegalArgumentException("NaN range.");
		if (numberOfTrapezoid <= 0) throw new IllegalArgumentException("numberOfTrapezoid must be > 0.");
		if (x1 == x2) return 0.0;
		if (x2 < x1) { double t = x1; x1 = x2; x2 = t; }

		final double EPS = 1e-9;
		final int N = numberOfTrapezoid;
		java.util.ArrayList<Double> grid = new java.util.ArrayList<>(N + 32);
		for (int k = 0; k <= N; k++) {
			double xk = x1 + (x2 - x1) * k / N;
			grid.add(xk);
		}

		java.util.ArrayList<Double> roots = new java.util.ArrayList<>();
		double prevX = grid.get(0);
		double prevD = f(p1, prevX) - f(p2, prevX);

		for (int i = 1; i < grid.size(); i++) {
			double currX = grid.get(i);
			double currD = f(p1, currX) - f(p2, currX);

			boolean signChange = (prevD == 0.0) || (currD == 0.0) || (prevD * currD < 0.0);
			if (signChange && (currX - prevX) > EPS) {
				double r = sameValue(p1, p2, prevX, currX, EPS);
				if (r > prevX + EPS && r < currX - EPS) {
					roots.add(r);
				}
			}
			prevX = currX;
			prevD = currD;
		}
		grid.addAll(roots);
		java.util.Collections.sort(grid);

		java.util.ArrayList<Double> xs = new java.util.ArrayList<>(grid.size());
		for (double x : grid) {
			if (xs.isEmpty() || Math.abs(x - xs.get(xs.size() - 1)) > 1e-12) {
				xs.add(x);
			}
		}

		boolean refined;
		int safeGuard = 0;
		do {
			refined = false;
			java.util.ArrayList<Double> insert = new java.util.ArrayList<>();
			for (int i = 1; i < xs.size(); i++) {
				double a = xs.get(i - 1), b = xs.get(i);
				double da = f(p1, a) - f(p2, a);
				double db = f(p1, b) - f(p2, b);
				if ((da == 0.0 || db == 0.0 || da * db < 0.0) && (b - a) > EPS) {
					double r = sameValue(p1, p2, a, b, EPS);
					if (r > a + EPS && r < b - EPS) {
						insert.add(r);
						refined = true;
					}
				}
			}
			if (refined) {
				xs.addAll(insert);
				java.util.Collections.sort(xs);
				java.util.ArrayList<Double> tmp = new java.util.ArrayList<>(xs.size());
				for (double x : xs) {
					if (tmp.isEmpty() || Math.abs(x - tmp.get(tmp.size() - 1)) > 1e-12) tmp.add(x);
				}
				xs = tmp;
			}
			safeGuard++;
		} while (refined && safeGuard < 4); 
		double area = 0.0;
		for (int i = 1; i < xs.size(); i++) {
			double a = xs.get(i - 1), b = xs.get(i);
			double fa = Math.abs(f(p1, a) - f(p2, a));
			double fb = Math.abs(f(p1, b) - f(p2, b));
			area += (b - a) * (fa + fb) * 0.5;
		}
		return area;
	}



	/**
	 * This function computes the array representation of a polynomial function from a String
	 * representation. Note:given a polynomial function represented as a double array,
	 * getPolynomFromString(poly(p)) should return an array equals to p.
	 * 
	 * @param p - a String representing polynomial function.
	 * @return
	 */
	
	/* Pseudocode:
     *  split string into terms by +/-
     *  parse coefficient and power for each term
     *  accumulate into array
     */
	public static double[] getPolynomFromString(String p) {
		if (p == null || p.trim().isEmpty()) return new double[]{0};

		p = p.replace(" ", "");

		java.util.List<String> terms = new java.util.ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < p.length(); i++) {
			char c = p.charAt(i);
			if ((c == '+' || c == '-') && sb.length() > 0) {
				terms.add(sb.toString());
				sb.setLength(0);
			}
			sb.append(c);
		}
		if (sb.length() > 0) terms.add(sb.toString());

		java.util.Map<Integer, Double> coeffMap = new java.util.HashMap<>();
		int maxPower = 0;

		for (String term : terms) {
			if (term.isEmpty()) continue;

			double sign = 1.0;
			if (term.charAt(0) == '-') {
				sign = -1.0;
				term = term.substring(1);
			} else if (term.charAt(0) == '+') {
				term = term.substring(1);
			}

			double coeff = 0.0;
			int power = 0;

			if (term.contains("x")) {
				String[] parts = term.split("x");
				if (parts[0].isEmpty()) coeff = 1.0;
				else coeff = Double.parseDouble(parts[0]);

				if (parts.length > 1 && parts[1].startsWith("^")) {
					power = Integer.parseInt(parts[1].substring(1));
				} else {
					power = 1;
				}
			} else {
				coeff = Double.parseDouble(term);
				power = 0;
			}

			coeff *= sign;
			coeffMap.put(power, coeffMap.getOrDefault(power, 0.0) + coeff);
			maxPower = Math.max(maxPower, power);
		}

		double[] ans = new double[maxPower + 1];
		for (int i = 0; i <= maxPower; i++) {
			ans[i] = coeffMap.getOrDefault(i, 0.0);
		}
		return ans;
	}

	/**
	 * This function computes the polynomial function which is the sum of two polynomial functions (p1,p2)
	 * @param p1
	 * @param p2
	 * @return
	 */
	
	/* Pseudocode:
     *  for each index i:
     *      ans[i] = p1[i] + p2[i] (0 if missing)
     */
	public static double[] add(double[] p1, double[] p2) {
		if (p1 == null || p2 == null) throw new IllegalArgumentException("Polynomials cannot be null.");
		int len = Math.max(p1.length, p2.length);
		double[] ans = new double[len];
		for (int i = 0; i < len; i++) {
			double c1 = (i < p1.length) ? p1[i] : 0.0;
			double c2 = (i < p2.length) ? p2[i] : 0.0;
			ans[i] = c1 + c2;
		}
		return ans;
	}


	/**
	 * This function computes the polynomial function which is the multiplication of two polynoms (p1,p2)
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double[] mul(double[] p1, double[] p2) {
		double [] ans = ZERO;//
        /** add you code below

         /////////////////// */
		return ans;
	}
	/**
	 * This function computes the derivative of the p0 polynomial function.
	 * @param po
	 * @return
	 */
	public static double[] derivative (double[] po) {
		double [] ans = ZERO;//
        /** add you code below

         /////////////////// */
		return ans;
	}
}
