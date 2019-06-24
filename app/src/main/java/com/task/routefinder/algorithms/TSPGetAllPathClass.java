package com.task.routefinder.algorithms;

import java.util.Vector;

public class TSPGetAllPathClass {

    Vector<Vector<Integer>> vector;
    int k;
    double graph[][];


    public TSPGetAllPathClass(double graph[][]) {
        vector=new Vector<>();
        k=0;
        this.graph=graph;
    }

    public String getMinimizedPath(int source) {
        int a[] = new int[graph.length];
        for (int i = 0; i < a.length; i++) {
            a[i] = i;
        }
        permute(a, 0, a.length - 1);

        System.out.println(vector.size());

        double min_path = Double.MAX_VALUE;
        String ans = "";
        for (int i = 0; i < vector.size(); i++) {
            double cuur_weight = 0;
            int k1 = source;
            String ss = "";
            for (int j = 0; j < vector.get(i).size(); j++) {
                cuur_weight += graph[k1][vector.get(i).get(j)];
//                char ch = vector.get(i).get(j)+48;
                ss += vector.get(i).get(j)+" ";
                k1 = vector.get(i).get(j);
            }
            if (min_path > cuur_weight) {
                min_path = cuur_weight;
                ans = ss;
            }
        }
        System.out.println("Minimum Cost: " + min_path);
        System.out.println("Odering of Nodes ");
        System.out.println(ans);
        ans=ans.trim();
        return ans+":"+(min_path/1000);
    }


    private void permute(int a[],int l,int r){
        if (l == r)
        {
            vector.add(new Vector<Integer>());
            for(int i=0;i<=r;i++)
            {
                vector.get(k).add(a[i]);
            }
            k++;
        }
        else
        {
            for (int i = l; i <= r; i++)
            {
                swap(a,l,i);
                permute(a, l+1, r);
                swap(a,l,i);
            }
        }
    }

    private void swap(int a[],int i,int j){
        int temp=a[i];
        a[i]=a[j];
        a[j]=temp;
    }

}
