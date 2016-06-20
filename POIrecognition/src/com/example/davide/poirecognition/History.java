package com.example.davide.poirecognition;

import java.util.ArrayList;

public class History {//lista di scan

	private ArrayList<Scan> history;
	public History(){
		history=new ArrayList<Scan>();
	}
	public ArrayList<Scan> getHistory(){
		return history;
	}
	public void setHistory(ArrayList<Scan> history){
		this.history = history;
	}
	public void add(Scan scan){
		history.add(scan);		
	}
	public int size(){
		return history.size();
	}
	public Scan getScan(int index){
		return history.get(index);
	}
	public void setScan(int index, Scan s){
		history.set(index, s);
	}
	public ArrayList<String> stringList(int index){//data lo scan ritorna una lista di mac 
		ArrayList<String> stringList= new ArrayList<String>();
		Scan listAccess= history.get(index);
		for(int a=0; a<listAccess.size(); a++)
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
		ApWeight swap;
		
		for(int a=0; a<list1.size(); a++)
		{
			wl.add(list1.getAp(a).getMac(), (double)0);			
		}
		for(int c=0; c<list2.size(); c++)
			{
				if(Functions.GetI(wl.stringList(), list2.getAp(c).getMac())==-1)
				{
					wl.add(list2.getAp(c).getMac(), (double)0);
				}				
			}
		
		for(int k = 0; k < wl.size(); k++)
		{
			double weight=0;
			int pos1 = Functions.GetI(stringList(i), wl.getApWeight(k).getApMac());
			int pos2 = Functions.GetI(stringList(j), wl.getApWeight(k).getApMac());
			if(pos1!=-1&&pos2!=-1)
			{
				pos1++;
				pos2++;
				weight = Functions.weight(pos1, pos2);
				wl.getApWeight(k).setWeight(weight);	
			}
			else
			{
				wl.getApWeight(k).setWeight(0);
			}	
			
		}
		
		for(int l=0; l<wl.size(); l++)
			{				
				for(int d=l; d<wl.size(); d++)
				{				
					if(wl.getApWeight(l).getWeight() < wl.getApWeight(d).getWeight())
					{
						swap = wl.getApWeight(l);
						wl.setApWeight(l, wl.getApWeight(d)); 
						wl.setApWeight(d, swap);						
					}
				}				
			}
		return wl;
	}
	
	public WeightList Merge(int index, WeightList wl)
	{
		Scan s=history.get(index);
		WeightList wl1 =new WeightList();
		for(int a=0; a<s.size(); a++)
		{
			wl1.add(s.getAp(a).getMac(), (double)0);			
		}
		for(int c=0; c<wl.size(); c++)
			{
				if(Functions.GetI(wl1.stringList(), wl.getApWeight(c).getApMac())==-1)
				{
					wl1.add(wl.getApWeight(c).getApMac(), (double)0);
				}				
			}
		ApWeight swap;
		for(int k = 0; k < wl1.size(); k++)
		{
			double weight=0;
			int pos1 = Functions.GetI(stringList(index), wl1.getApWeight(k).getApMac());
			int pos2 = Functions.GetI( wl.stringList(),wl1.getApWeight(k).getApMac());
			if(pos1!=-1&&pos2!=-1)
			{
				pos1++;
				pos2++;
				weight =Functions.weight(pos1, pos2);
				wl1.getApWeight(k).setWeight(weight);
			}
			else
			{
				wl1.getApWeight(k).setWeight(0);
			}			
			
		}
		for(int m=0; m<wl1.size(); m++)
		{
			if(Functions.GetI(wl.stringList(),wl1.getApWeight(m).getApMac())!=-1)
			{
				int in=Functions.GetI(wl.stringList(),wl1.getApWeight(m).getApMac());
				wl1.getApWeight(m).setWeight((wl1.getApWeight(m).getWeight()+(index)*wl.getApWeight(in).getWeight())/(double)(index+1));
			}
			else
			{
				wl1.getApWeight(m).setWeight(0);
			}
		}
		for(int l=0; l<wl1.size(); l++)
			for(int d=l; d<wl1.size(); d++)
			{			
				{			
					if(wl1.getApWeight(l).getWeight() < wl1.getApWeight(d).getWeight())
					{
						swap = wl1.getApWeight(l);
						wl1.setApWeight(l, wl1.getWeightsList().get(d)); 
						wl1.setApWeight(d, swap);						
					}			
				}
			}
		return wl1;
	}
}

