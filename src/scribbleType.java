import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class scribbleType {
	
	private static final double TEMPLATE_BORDER = 0.06;
	
	public static void main(String[] args) throws IOException {
		BufferedImage template = null;
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter file name: ");
		template = ImageIO.read(new File(scan.next()));
		
		BufferedImage[][] letters = splitLetters(template);
		
		for (int i = 0; i < letters.length; i++) {
			for (int j = 0; j < letters[i].length; j++) {
				String filename = ("letter" + i + "_" + j + ".jpg");
				ImageIO.write(letters[i][j], "jpg", new File(filename));
			}
		}
		
		System.out.print("Enter width of canvas in characters: ");
		int canvasWidth = scan.nextInt();
		System.out.print("Enter height of canvas in characters: ");
		int canvasHeight = scan.nextInt();
		scan.close();
	
		BufferedImage scribble = convertTextToImage("input.txt", canvasWidth, 
				canvasHeight, letters);
		ImageIO.write(scribble, "jpg", new File("output.jpg"));
	}
	
	public static BufferedImage[][] splitLetters(BufferedImage template) {
		BufferedImage[][] letters = new BufferedImage[26][6];
		int templateHeight = template.getHeight();
		int templateWidth = template.getWidth();
		BufferedImage singleLetter;
		
		for (int i = 0; i < 14; i++) {
			for (int j = 0; j < 11; j++) {
				singleLetter = template.getSubimage((int) (templateWidth * (i + TEMPLATE_BORDER) / 14),
						(int) (templateHeight * (j + TEMPLATE_BORDER) / 11), 
						(int) (templateWidth * (1 - 2 * TEMPLATE_BORDER) / 14), 
						(int) (templateHeight * (1 - 2 * TEMPLATE_BORDER) / 11));
				int letterIndex = (j*14 + i) / 6;
				int typeIndex = (j*14 + i) % 6;
				letters[letterIndex][typeIndex] = singleLetter;
			}
		}
		
		letters[25][4] = letters[25][2]; //copies "z" and "Z" to avoid null elements
		letters[25][5] = letters[25][2]; //do not use index 2 and 5 to maintain proper
		letters[25][2] = letters[25][1]; //weighting of the letter z	
		return letters;
	}
	
	public static BufferedImage convertTextToImage(String filename,
			int canvasWidth, int canvasHeight, BufferedImage[][] letters) 
					throws FileNotFoundException {
		
		int letterWidth = letters[0][0].getWidth();
		int letterHeight = letters[0][0].getHeight();
		int type = letters[0][0].getType();
		BufferedImage scribble = new BufferedImage(canvasWidth * letterWidth,
				canvasHeight * letterHeight, type);
		scribble.createGraphics().setPaint(Color.WHITE);
		scribble.createGraphics().fillRect(0, 0, 
				canvasWidth * letterWidth, canvasHeight * letterHeight);
		Scanner scan = new Scanner(new File(filename));
		String text = "";
		while (scan.hasNext()) {
			text += scan.next();
			if (scan.hasNext()) {
				text += " ";
			}
		}
		scan.close();
		for (int i = 0; i < text.length(); i++) {
			int letterIndex = 0;
			int typeIndex = 0;
			if (Character.isLowerCase(text.charAt(i))) {
				letterIndex = text.charAt(i) - 'a';
				typeIndex = (int) (Math.random() * 3);
			}
			else if (Character.isUpperCase(text.charAt(i))){
				letterIndex = text.charAt(i) - 'A';
				typeIndex = (int) (Math.random() * 3) + 3;
			}
			if (Character.isAlphabetic(text.charAt(i))) {
				scribble.createGraphics().drawImage(letters[letterIndex][typeIndex], null, 
						(i % canvasWidth) * letterWidth, (i / canvasWidth) * letterHeight);
			}
		}
		return scribble;
	}
}
