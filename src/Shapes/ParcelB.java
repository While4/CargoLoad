package Shapes;

public class ParcelB extends ParcelShape{
	
	protected static ShapeColor color = ShapeColor.BLUE;
	protected static int width = 3;
	protected static int height = 3;
	protected static int length = 3;
	
	protected static int value = 3;
	
	public ParcelB() {
		super(color, width, height, length, value);
	}
}
