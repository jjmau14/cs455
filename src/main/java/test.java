public class test {
	public static void main(String[] args){
		System.out.print(String.format("| %-8s ", "Node ID"));
		System.out.print(String.format("| %-15s ", "Packets Received"));
		System.out.print(String.format("| %-12s ", "Packets Sent"));
		System.out.print(String.format("| %-8s ", "Sum Sent"));
		System.out.println(String.format("| %-12s | ", "Sum Received"));
		System.out.println("========================================================================");
	}
}
