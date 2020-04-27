package core.query;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static java.lang.Double.isNaN;

public class Cluster {
    public static String[] build_vocab(HashMap<String,String[]> collection_items)
    {
        HashSet<String> vocab = new HashSet<String>();
        int file_id=0;
        for(String filename:collection_items.keySet())
        {
            for(String s:collection_items.get(filename))
                vocab.add(s);
//			System.out.println(file_id);
            file_id++;
        }
        return vocab.toArray(new String[vocab.size()]);
    }
    public static double correlation(String[] text,String v1,String v2)
    {
        double c=0.0;
        double cr=0.0;
        for(String str1:text)
        {
            cr=0.0;
            for(String str2:text)
            {
                cr+=1;
                if (str1.equals(v1)&&str2.equals(v2))
                    c+=(1/(cr-1));
            }
        }
        return c;
    }
    public static double counts(String str,String[] strs)
    {
        double ct=0.0;
        for(String s:strs)
            if(s.equals(str))
                ct++;
        return ct;
    }
    public static void build_Association(HashMap<String,String[]> collection_items,String[] vocabArray,String[] query_vocab) throws Exception
    {
        BufferedWriter br = new BufferedWriter(new FileWriter("association.txt"));
        br.write("word1\tword2\tcorrelation\n");
        double c1,c2,c3;
        int i=0;
        for(;i<vocabArray.length;i++)
        {
//			System.out.println(vocabArray.length-i);
            for(String j:query_vocab)
            {
                Pair p = new Pair(vocabArray[i],j);
                c1=0.0;
                c2=0.0;
                c3=0.0;
                for(String filename:collection_items.keySet())
                {
                    c1+=counts(p.s1,collection_items.get(filename))*counts(p.s2,collection_items.get(filename));
                    c2+=counts(p.s1,collection_items.get(filename))*counts(p.s1,collection_items.get(filename));
                    c3+=counts(p.s2,collection_items.get(filename))*counts(p.s2,collection_items.get(filename));
                }
                c1/=(c1+c2+c3);
                if(c1!=0)
                    br.write(p.s1+"\t"+p.s2+"\t"+c1+"\n");
            }
        }
        br.close();
    }
    public static void build_Metric(HashMap<String,String[]> collection_items,String[] vocabArray) throws Exception
    {
        HashMap<Pair,Double> correlations = new HashMap<Pair,Double>();
        HashMap<String,Double> sizes = new HashMap<String,Double>();
        double c=0.0;
        int i=0,j;
        for(;i<vocabArray.length;i++)
        {
            j=0;
            for(String filename:collection_items.keySet())
                j+=counts(vocabArray[i],collection_items.get(filename));
            sizes.put(vocabArray[i],(double)j);
        }
        for(i=0;i<vocabArray.length;i++)
            for(j=i;j>=0;j--)
            {
                Pair p = new Pair(vocabArray[i],vocabArray[j]);
                correlations.put(p,0.0);
            }
        for(Pair p:correlations.keySet())
        {
            for(String filename:collection_items.keySet())
                c=correlation(collection_items.get(filename),p.s1,p.s2)/(sizes.get(p.s1)+sizes.get(p.s2));
            if(isNaN(c))
                c=1.0;
            correlations.put(p,c);
        }
        BufferedWriter br = new BufferedWriter(new FileWriter("metric.txt"));
        br.write("word1\tword2\tcorrelation\n");
        for(Pair p:correlations.keySet())
            br.write(p.s1+"\t"+p.s2+"\t"+correlations.get(p)+"\n");
        br.close();
    }
    public static void build_Scalar(HashMap<String,String[]> collection_items,String[] vocabArray) throws Exception
    {
        HashMap<Pair,Double> correlations = new HashMap<Pair,Double>();
        HashMap<String,Double> sizes = new HashMap<String,Double>();
        double c,c1,c2;
        ArrayList<Double> d = new ArrayList<Double>();
        HashMap<String,ArrayList<Double> > correlation_vectors = new HashMap<String,ArrayList<Double> >();
        int i=0,j;
        for(;i<vocabArray.length;i++)
        {
            j=0;
            for(String filename:collection_items.keySet())
                j+=counts(vocabArray[i],collection_items.get(filename));
            sizes.put(vocabArray[i],(double)j);
        }
        for(i=0;i<vocabArray.length;i++)
            for(j=i;j>=0;j--)
            {
                Pair p = new Pair(vocabArray[i],vocabArray[j]);
                correlations.put(p,0.0);
            }
        for(Pair p:correlations.keySet())
        {
            c=0.0;
            for(String filename:collection_items.keySet())
            {
                d = new ArrayList<Double>();
                c=correlation(collection_items.get(filename),p.s1,p.s2)/(sizes.get(p.s1)+sizes.get(p.s2));
                d.add(c);
            }
            correlation_vectors.put(p.s1,d);
        }
        for(Pair p:correlations.keySet())
        {
            c=0.0;
            c1=0.0;
            c2=0.0;
            ArrayList<Double> p1 = correlation_vectors.get(p.s1);
            ArrayList<Double> p2 = correlation_vectors.get(p.s2);
            for(i=0;i<p1.size();i++)
            {
                c+=p1.get(i)*p2.get(i);
                c1+=p1.get(i)*p1.get(i);
                c2+=p2.get(i)*p2.get(i);
            }
            c=c/(c1*c2);
            if(isNaN(c))
                c=1.0;
            correlations.put(p,c);
        }
        BufferedWriter br = new BufferedWriter(new FileWriter("scalar.txt"));
        br.write("word1\tword2\tcorrelation\n");
        for(Pair p:correlations.keySet())
            br.write(p.s1+"\t"+p.s2+"\t"+correlations.get(p)+"\n");
        br.close();
    }
}
