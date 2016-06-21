
package Balancity.data;


public class convertRijksDriehoek
{
	// The city "Amsterfoort" is used as reference "Rijksdriehoek" coordinate.
	public static int referenceRdX = 155000;
	public static int referenceRdY = 463000;
	// The city "Amsterfoort" is used as reference "WGS84" coordinate
	public static double referenceWgs84X = 52.15517;
	public static double referenceWgs84Y = 5.387206;
	
	//Code translated from C# code by Roel van Lisdonk from https://www.roelvanlisdonk.nl/?p=2950
	// or the idea from: Eenvoudig RD-coordinaten berekenen uit GPS by ing. F.H. Schreutelkamp, Stichting ‘De Koepel’, sterrenwacht ‘Sonnenborgh’ te Utrecht, en ir. G.L. Strang van Hees, voormalig universitair docent van de afdeling Geodesie, TU Delft.
	public static double[] ConvertToLatLong(double x, double y)
	{


		double dX = (double)(x - referenceRdX) * (double)(Math.pow(10,-5));
		double dY = (double)(y - referenceRdY) * (double)(Math.pow(10,-5));

		double sumN = 
			(3235.65389 * dY) + 
			(-32.58297 * Math.pow(dX, 2)) + 
			(-0.2475 * Math.pow(dY, 2)) + 
			(-0.84978 * Math.pow(dX, 2) * dY) + 
			(-0.0655 * Math.pow(dY, 3)) + 
			(-0.01709 * Math.pow(dX, 2) * Math.pow(dY, 2)) + 
			(-0.00738 * dX) + 
			(0.0053 * Math.pow(dX, 4)) + 
			(-0.00039 * Math.pow(dX, 2) * Math.pow(dY, 3)) + 
			(0.00033 * Math.pow(dX, 4) * dY) + 
			(-0.00012 * dX * dY);
		double sumE = 
			(5260.52916 * dX) + 
			(105.94684 * dX * dY) + 
			(2.45656 * dX * Math.pow(dY, 2)) + 
			(-0.81885 * Math.pow(dX, 3)) + 
			(0.05594 * dX * Math.pow(dY, 3)) + 
			(-0.05607 * Math.pow(dX, 3) * dY) + 
			(0.01199 * dY) + 
			(-0.00256 * Math.pow(dX, 3) * Math.pow(dY, 2)) + 
			(0.00128 * dX * Math.pow(dY, 4)) + 
			(0.00022 * Math.pow(dY, 2)) + 
			(-0.00022 * Math.pow(dX, 2)) + 
			(0.00026 * Math.pow(dX, 5));

		double latitude = referenceWgs84X + (sumN / 3600);
		double longitude = referenceWgs84Y + (sumE / 3600);
		double[] result = {latitude,longitude};

		return result;
	}
	
	
	//Code translated from reaction on https://www.roelvanlisdonk.nl/?p=2950
	// or the idea from: Eenvoudig RD-coordinaten berekenen uit GPS by ing. F.H. Schreutelkamp, Stichting ‘De Koepel’, sterrenwacht ‘Sonnenborgh’ te Utrecht, en ir. G.L. Strang van Hees, voormalig universitair docent van de afdeling Geodesie, TU Delft.
	public static int[] convertToRD(double latitude, double longitude)
	{

		
		double[][] Rpq = new double[4][5];
		Rpq[0][1] = 190094.945;
		Rpq[1][1] = -11832.228;
		Rpq[2][1] = -114.221;
		Rpq[0][3] = -32.391;
		Rpq[1][0] = -0.705;
		Rpq[3][1] = -2.340;
		Rpq[0][2] = -0.008;
		Rpq[1][3] = -0.608;
		Rpq[2][3] = 0.148;

		double[][] Spq = new double[4][5];
		Spq[0][1] = 0.433;
		Spq[0][2] = 3638.893;
		Spq[0][4] = 0.092;
		Spq[1][0] = 309056.544;
		Spq[2][0] = 73.077;
		Spq[1][2] = -157.984;
		Spq[3][0] = 59.788;
		Spq[2][2] = -6.439;
		Spq[1][1] = -0.032;
		Spq[1][4] = -0.054;

		double d_lattitude = (0.36 *(latitude - referenceWgs84X));
		double d_longitude = (0.36 *(longitude - referenceWgs84Y));

		double calc_latt = 0;
		double calc_long = 0;

		for (int p = 0; p < 4; p++)
		{
			for (int q = 0; q < 5; q++)
			{
				calc_latt += Rpq[p][q] * Math.pow(d_lattitude, p) * Math.pow(d_longitude, q);
				calc_long += Spq[p][q] * Math.pow(d_lattitude, p) * Math.pow(d_longitude, q);
			}
		}

		int rd_x_coordinate = (int)Math.round(referenceRdX + calc_latt);
		int rd_y_coordinate = (int)Math.round(referenceRdY + calc_long);
		int result[] = {rd_x_coordinate,rd_y_coordinate};
		return result;
	}
}
