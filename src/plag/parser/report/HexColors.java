package plag.parser.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HexColors 
{
	HashMap<String, String> hexColors;
	ArrayList<String> colors = new ArrayList<String>();
	public HexColors()
	{
		colors.add("#0000FF"); //	Blue "; 
		colors.add("#8A2BE2"); //	BlueViolet "; 
		colors.add("#A52A2A"); //	Brown "; 
		colors.add("#DEB887"); //	BurlyWood "; 
		colors.add("#5F9EA0"); //	CadetBlue "; 
		colors.add("#7FFF00"); //	Chartreuse "; 
		colors.add("#D2691E"); //	Chocolate "; 
		colors.add("#FF7F50"); //	Coral "; 
		colors.add("#6495ED"); //	CornflowerBlue "; 
		colors.add("#DC143C"); //	Crimson "; 
		colors.add("#00008B"); //	DarkBlue "; 
		colors.add("#008B8B"); //	DarkCyan "; 
		colors.add("#B8860B"); //	DarkGoldenRod "; 
		colors.add("#A9A9A9"); //	DarkGray "; 
		colors.add("#006400"); //	DarkGreen "; 
		colors.add("#BDB76B"); //	DarkKhaki "; 
		colors.add("#8B008B"); //	DarkMagenta "; 
		colors.add("#FF8C00"); //	DarkOrange "; 
		colors.add("#8B0000"); //	DarkRed "; 
		colors.add("#E9967A"); //	DarkSalmon "; 
		colors.add("#8FBC8F"); //	DarkSeaGreen "; 
		colors.add("#00CED1"); //	DarkTurquoise "; 
		colors.add("#9400D3"); //	DarkViolet "; 
		colors.add("#FF1493"); //	DeepPink "; 
		colors.add("#1E90FF"); //	DodgerBlue "; 
		colors.add("#B22222"); //	FireBrick "; 
		colors.add("#228B22"); //	ForestGreen "; 
		colors.add("#FF00FF"); //	Fuchsia "; 
		colors.add("#FFD700"); //	Gold "; 
		colors.add("#DAA520"); //	GoldenRod "; 
		colors.add("#808080"); //	Gray "; 
		colors.add("#008000"); //	Green "; 
		colors.add("#ADFF2F"); //	GreenYellow "; 
		colors.add("#FF69B4"); //	HotPink "; 
		colors.add("#CD5C5C"); //	IndianRed  "; 
		colors.add("#4B0082"); //	Indigo  "; 
		colors.add("#F0E68C"); //	Khaki "; 
//		colors.add("#ADD8E6"); //	LightBlue "; 
//		colors.add("#F08080"); //	LightCoral "; 
//		colors.add("#E0FFFF"); //	LightCyan "; 
//		colors.add("#D3D3D3"); //	LightGray "; 
//		colors.add("#90EE90"); //	LightGreen "; 
//		colors.add("#FFB6C1"); //	LightPink "; 
//		colors.add("#FFA07A"); //	LightSalmon "; 
//		colors.add("#20B2AA"); //	LightSeaGreen "; 
//		colors.add("#87CEFA"); //	LightSkyBlue "; 
//		colors.add("#778899"); //	LightSlateGray "; 
//		colors.add("#B0C4DE"); //	LightSteelBlue "; 
//		colors.add("#32CD32"); //	LimeGreen "; 
		colors.add("#FF00FF"); //	Magenta "; 
		colors.add("#800000"); //	Maroon "; 
		colors.add("#66CDAA"); //	MediumAquaMarine "; 
		colors.add("#BA55D3"); //	MediumOrchid "; 
		colors.add("#FFE4E1"); //	MistyRose "; 
		colors.add("#FFE4B5"); //	Moccasin "; 
		colors.add("#000080"); //	Navy "; 
		colors.add("#808000"); //	Olive "; 
		colors.add("#FFA500"); //	Orange "; 
		colors.add("#FF4500"); //	OrangeRed "; 
		colors.add("#DA70D6"); //	Orchid "; 
		colors.add("#98FB98"); //	PaleGreen "; 
		colors.add("#AFEEEE"); //	PaleTurquoise "; 
		colors.add("#DB7093"); //	PaleVioletRed "; 
		colors.add("#CD853F"); //	Peru "; 
		colors.add("#DDA0DD"); //	Plum "; 
		colors.add("#800080"); //	Purple "; 
		colors.add("#BC8F8F"); //	RosyBrown "; 
		colors.add("#4169E1"); //	RoyalBlue "; 
		colors.add("#8B4513"); //	SaddleBrown "; 
		colors.add("#FA8072"); //	Salmon "; 
		colors.add("#F4A460"); //	SandyBrown "; 
		colors.add("#2E8B57"); //	SeaGreen "; 
		colors.add("#A0522D"); //	Sienna "; 
		colors.add("#87CEEB"); //	SkyBlue "; 
		colors.add("#6A5ACD"); //	SlateBlue "; 
		colors.add("#708090"); //	SlateGray "; 
		colors.add("#00FF7F"); //	SpringGreen "; 
		colors.add("#4682B4"); //	SteelBlue "; 
		colors.add("#D2B48C"); //	Tan "; 
		colors.add("#008080"); //	Teal "; 
		colors.add("#D8BFD8"); //	Thistle "; 
		colors.add("#FF6347"); //	Tomato "; 
		colors.add("#40E0D0"); //	Turquoise "; 
		colors.add("#EE82EE"); //	Violet "; 
		colors.add("#F5DEB3"); //	Wheat "; 
		colors.add("#FFFF00"); //	Yellow "; 
		colors.add("#9ACD32"); //	YellowGreen "; 
	}
	
	public HashMap<Integer , String> GetRandomHexColor(int size)
	{
		HashMap<Integer , String> Colors = new HashMap<Integer, String>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		int count = 0;
		
		while(count != size)
		{
			int index = randInt(1,93);
			if(!indices.contains(index))
			{
				indices.add(index);
				count++;
			}
		}
		
		Integer[] arrAll = new Integer[indices.size()];
		indices.toArray(arrAll);
		for(int i = 0; i < indices.size(); i++)
		{
			Colors.put(i, colors.get(i));
		}
		
		return Colors;
	}
	
	public int randInt(int min, int max) 
	{
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}
