package coding;
import java.util.Scanner;

public class PrimeNumbers {
	//method that tells number is prime or not
	public static boolean isPrime(int number){
		for(int i=2;i<=number/2;i++){
			if(number % i==0){
				return false;
			}
		}
		return true;
	}

	//wrapper for System.exit
	public static void exitNow(){
		System.out.println("Exiting Now");
		System.exit(0);
	}

	//Logger or wrapper for printing
	public static void Log(String S){
		System.out.println(S);
	}

	public static void main(String[] args) {
		int number=1;
		int count = 1;
		int startnumber = 1;
		//System.out.println(number.isDigit());
		Scanner sc = new Scanner(System.in);
		Log("Enter the value of number:");
		try{
			number = sc.nextInt();
		}
		catch(Exception e){
			Log("Invalid Input! Enter numbers between 1-100");
			exitNow();
		}
	    if(number<=0){
	    	Log("Invalid Number! Enter 1 to 100");
	    }
	    else if(number>100){
	    	Log("Invalid Number! Enter 1 to 100");
	    	exitNow();
	    }
	    else if(number>0&&number<=100){
	    	while(count<=number){
				startnumber++;
				if(PrimeNumbers.isPrime(startnumber)){
					Log("The prime number " + count +" is "+startnumber);
					count++;
				} //end If
			} //end While
	    }//end Else
	    else{
	    	//empty or other condition
	    } //Final else
	} //end Main
} //end class
