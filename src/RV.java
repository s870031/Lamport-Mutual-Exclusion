import java.lang.*;
import java.util.Scanner;

public class RV
{
	static double Seed = 1111.0;
	static double uni_rv(){
		double k = 16807.0;
		double m = 2.147483647e9;
		double rv;

		Seed=(k*Seed)%m;
		rv=Seed/m;
		return (rv);
	}
	static double exp_rv(double lambda){
		double exp;
		exp = ((-1) / lambda) * Math.log(uni_rv());
		return(exp);
	}
	/*public static void main(String args[]){
		Scanner reader = new Scanner(System.in);
		double mean = reader.nextDouble();

		System.out.println(uni_rv());
		System.out.println(exp_rv((double)1/mean));
	}*/
}
