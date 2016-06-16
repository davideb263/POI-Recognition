package com.example.davide.poirecognition;

import java.util.ArrayList;

public class History {

	private ArrayList<Scan> history;
	public History()
	{
		history=new ArrayList<Scan>();
	}
	public ArrayList<Scan> getHistory() {
		return history;
	}
	public void setHistory(ArrayList<Scan> history) {
		this.history = history;
	}
	public void add(Scan scan)
	{
		history.add(scan);		
	}
	public int size()
	{
		return history.size();
	}
	public Scan getScan(int index)
	{
		return history.get(index);
	}
	public void setScan(int index, Scan s)
	{
		history.set(index, s);
	}
	public ArrayList<String> stringList(int index)
	{
		ArrayList<String> stringList= new ArrayList<String>();
		Scan listAccess= history.get(index);
		for(int a=0; a<listAccess.Size(); a++)
		{
			stringList.add(listAccess.getAp(a).getMac());

		}
			return stringList;
	}
	
	public WeightList firstMerge(int i, int j)
	{
		Scan list1=history.get(i);
		Scan list2= history.get(j);
		WeightList wl=new WeightList();
		for(int a=0; a<list1.Size(); a++)
		{
			wl.Add(list1.getAp(a).getMac(), (double)0);			
		}
		for(int c=0; c<list2.Size(); c++)
			{
				if(Functions.GetI(wl.stringList(), list2.getAp(c).getMac())==-1)
				{
					wl.Add(list2.getAp(c).getMac(), (double)0);
				}				
			}
		apWeight swap;
		for(int k = 0; k < wl.Size(); k++)
		{
			double weight=0;
			int pos1 = Functions.GetI(stringList(i), wl.getWeightsList().get(k).getApMac());
			int pos2 = Functions.GetI(stringList(j), wl.getWeightsList().get(k).getApMac());
			if(pos1!=-1&&pos2!=-1)
			{
				pos1++;
				pos2++;
			weight = Functions.weight(pos1, pos2);
			wl.getWeightsList().get(k).setWeight(weight);	
			}
			else
			{
				wl.getWeightsList().get(k).setWeight(0);
			}	
			
		}
		
		for(int l=0; l<wl.Size(); l++)
			{
				
				for(int d=l; d<wl.Size(); d++)
				{				
					if(wl.getWeightsList().get(l).getWeight() < wl.getWeightsList().get(d).getWeight())
					{
						swap = wl.getWeightsList().get(l);
						wl.getWeightsList().set(l, wl.getWeightsList().get(d)); 
						wl.getWeightsList().set(d, swap);
						
					}
				}
				
			}
		return wl;
	}
	
	public WeightList Merge(int index, WeightList wl)
	{
		Scan s=history.get(index);
		WeightList wl1 =new WeightList();
		for(int a=0; a<s.Size(); a++)
		{
			wl1.Add(s.getAp(a).getMac(), (double)0);			
		}
		for(int c=0; c<wl.Size(); c++)
			{
				if(Functions.GetI(wl1.stringList(), wl.getWeightsList().get(c).getApMac())==-1)
				{
					wl1.Add(wl.getWeightsList().get(c).getApMac(), (double)0);
				}				
			}
		apWeight swap;
		for(int k = 0; k < wl1.Size(); k++)
		{
			double weight=0;
			int pos1 = Functions.GetI(stringList(index), wl1.getApWeight(k).getApMac());
			int pos2 = Functions.GetI( wl.stringList(),wl1.getApWeight(k).getApMac());
			if(pos1!=-1&&pos2!=-1)
			{
				pos1++;
				pos2++;
			weight =Functions.weight(pos1, pos2);
			wl1.getWeightsList().get(k).setWeight(weight);
			}
			else
			{
				wl1.getWeightsList().get(k).setWeight(0);
			}			
			
		}
		for(int m=0; m<wl1.Size(); m++)
		{
			if(Functions.GetI(wl.stringList(),wl1.getApWeight(m).getApMac())!=-1)
			{
				int in=Functions.GetI(wl.stringList(),wl1.getApWeight(m).getApMac());
				wl1.getWeightsList().get(m).setWeight((wl1.getApWeight(m).getWeight()+(index)*wl.getApWeight(in).getWeight())/(double)(index+1));
			}
			else
			{
				wl1.getWeightsList().get(m).setWeight(0);
			}
		}
		for(int l=0; l<wl1.Size(); l++)
			for(int d=l; d<wl1.Size(); d++)
			{			
				{			
					if(wl1.getWeightsList().get(l).getWeight() < wl1.getWeightsList().get(d).getWeight())
					{
						swap = wl1.getWeightsList().get(l);
						wl1.getWeightsList().set(l, wl1.getWeightsList().get(d)); 
						wl1.getWeightsList().set(d, swap);						
					}			
				}
			}
		return wl1;
	}
}
