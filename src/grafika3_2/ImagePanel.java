package grafika3_2;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
class ImagePanel extends JPanel 
{
	static Graphics2D g2d;
    private BufferedImage image;
    private BufferedImage transImage;
    
    int x_res;
    int y_res;
    
    int x_resTrans;
    int y_resTrans;
    
    public ImagePanel()
    {
    	image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    	transImage = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
    	x_res = image.getWidth();
    	y_res = image.getHeight();
    	x_resTrans = transImage.getWidth();
    	y_resTrans = transImage.getHeight();
    	transform();
    }
    
    public ImagePanel(BufferedImage image) 
    {
        this.image = image;
        transImage = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
    	x_res = image.getWidth();
    	y_res = image.getHeight();
    	x_resTrans = transImage.getWidth();
    	y_resTrans = transImage.getHeight();
    	transform();
    }
    
    public void setImage(BufferedImage newImg)
    {
    	image = newImg;
    	transform();
    }
    
    public void transform()
    {
    	double[][] transMatrix = loadMatrix();
		double[][] transMatrixInv = inverse(transMatrix);
		
		for(int i = 0; i < x_resTrans; i++)
		{
			for(int j = 0; j < y_resTrans; j++)
			{
				transImage.setRGB(i, j, int2RGB(255,255,255));
			}
		}
		
		for(int i = 0; i < x_resTrans; i++)
		{
			for(int j = 0; j < y_resTrans; j++)
			{
				double[] currPoint = new double[3];
				currPoint[0] = i;
				currPoint[1] = j;
				currPoint[2] = 1;
				double[] originalPoint = new double[3];
				for(int k = 0; k < transMatrix[0].length; k++)
				{
					int val = 0;
					for(int l = 0; l < transMatrix.length; l++)
					{
						val += currPoint[l] * transMatrixInv[l][k];
					}
					originalPoint[k] = val;
				}
				try
				{
					transImage.setRGB(i, j, image.getRGB((int)originalPoint[0], (int)originalPoint[1]));
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					;
				}
			}
		}
		
		try
		{
			ImageIO.write(transImage, "png", new File("cat2Trans.png"));
			System.out.println("Transformed image created successfully!");
		}
		catch(IOException e)
		{
			System.out.println("The image cannot be stored");
		}
    }
    
    private double[][] inverse(double[][] mat)
    {
    	double[][] out = new double[mat.length][mat[0].length];
    	double det = 0;
    	int i;
    	int j;
    	for(i = 0; i < 3; i++)
    	{
	        det = det + (mat[0][i] * (mat[1][(i+1)%3] * mat[2][(i+2)%3] - mat[1][(i+2)%3] * mat[2][(i+1)%3]));
    	}
		for(i = 0; i < 3; ++i) 
		{
			for(j = 0; j < 3; ++j)
			{
				out[i][j] = (((mat[(j+1)%3][(i+1)%3] * mat[(j+2)%3][(i+2)%3]) 
						- (mat[(j+1)%3][(i+2)%3] * mat[(j+2)%3][(i+1)%3]))/ det);
			}
		}
		return out;
    }
    
    private double[][] loadMatrix()
	{
		double[][] out = new double[3][3];
		
		String formFile = null;
		try 
		{
			formFile = readFile("transMatrix.txt");
		} 
		catch (IOException e) 
		{
			System.out.println("Error while loading file (vectorImage.txt)");
		}
		
		String[] values = formFile.split(",");
		if(values.length % 5 != 0)
		{
			System.out.println("Error - txt format");
		}
		else
		{
			if(values.length > 5)
			{
				int numOfM = values.length/5;
				
				for(int i = 0; i < numOfM; i++)
				{
					double[][] temp = new double[3][3];
					
					temp[0][0] = Double.parseDouble(values[5*i + 1]) * Math.cos(Math.toRadians(Double.parseDouble(values[5*i + 0])));
					temp[0][1] = Math.sin(Math.toRadians(Double.parseDouble(values[5*i + 0])));
					temp[0][2] = 0;
					temp[1][0] = (-Math.sin(Math.toRadians(Double.parseDouble(values[5*i + 0]))));
					temp[1][1] = Double.parseDouble(values[5*i + 2]) * Math.cos(Math.toRadians(Double.parseDouble(values[5*i + 0])));
					temp[1][2] = 0;
					temp[2][0] = Double.parseDouble(values[5*i + 3]);
					temp[2][1] = Double.parseDouble(values[5*i + 4]);
					temp[2][2] = 1;
					
					if(i == 0)
					{
						out = temp;
					}
					else
					{
						out = matrixMul(out, temp);
					}
				}
				
			}
			else
			{
				out[0][0] = Double.parseDouble(values[1]) * Math.cos(Math.toRadians(Double.parseDouble(values[0])));
				out[0][1] = Math.sin(Math.toRadians(Double.parseDouble(values[0])));
				out[0][2] = 0;
				out[1][0] = (-Math.sin(Math.toRadians(Double.parseDouble(values[0]))));
				out[1][1] = Double.parseDouble(values[2]) * Math.cos(Math.toRadians(Double.parseDouble(values[0])));
				out[1][2] = 0;
				out[2][0] = Double.parseDouble(values[3]);
				out[2][1] = Double.parseDouble(values[4]);
				out[2][2] = 1;
			}
			
			if(out[0][0] == 0 || out[1][1] == 0)
			{
				out[0][0] = 1;
				out[1][1] = 1;
			}
		}
		
		return out;
	}

    private double[][] matrixMul(double[][] A, double[][] B) 
	{

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) 
        {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] C = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) 
        {
            for (int j = 0; j < bColumns; j++) 
            {
                C[i][j] = (double)0;
            }
        }

        for (int i = 0; i < aRows; i++) 
        {
            for (int j = 0; j < bColumns; j++) 
            {
                for (int k = 0; k < aColumns; k++) 
                {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }

    private String readFile(String path) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}
    
    @Override
    protected void paintComponent(Graphics g) 
    {
    	super.paintComponent(g);
		g2d = (Graphics2D) g;
		g2d.drawImage(transImage, 0, 0, transImage.getWidth(), transImage.getHeight(), this);
		//g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), this);
    }
    
    private int int2RGB( int red, int green, int blue)
	{
		red = red & 0x000000FF;
		green = green & 0x000000FF;
		blue = blue & 0x000000FF;
		return (red << 16) + (green << 8) + blue;
	}
}
