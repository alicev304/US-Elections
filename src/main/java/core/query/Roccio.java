package core.query;

import utils.Utils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Roccio {
    public static String return_best(Query q, Document[] dp, Document[] dn)
    {
        HashSet<String> q_final = new HashSet<String>();
        Map<String, Double> vector = new LinkedHashMap<>();
        int ct=2;
        String return_query="";
        for(String s:q.getVector().keySet())
            vector.put(s,0.0);
        for(Document d:dp)
            for(String s:d.getVector().keySet())
                vector.put(s,0.0);
        for(Document d:dn)
            for(String s:d.getVector().keySet())
                vector.put(s,0.0);
        for(String s:vector.keySet())
            vector.put(s,rocchio_score(s,q,dp,dn));
        Map<String, Double> new_vector = Utils.sortMap(vector);
        for(String s:q.getVector().keySet())
            q_final.add(s);
        for(String s:new_vector.keySet())
            q_final.add(s);
        for(String s:q_final)
        {
            return_query+=s+" ";
            ct--;
            if(ct==0)
                break;
        }
        return return_query;
    }

    public static double rocchio_score(String s, Query q, Document[] dp, Document[] dn)
    {
        double alpha = q.getVector().get(s);
        double beta = 0.0;
        double gamma = 0.0;
        for(Document d:dp)
        {
            for(String str:d.getVector().keySet())
                if(s.equals(str))
                    beta+=d.getVector().get(str);
        }
        for(Document d:dn)
        {
            for(String str:d.getVector().keySet())
                if(s.equals(str))
                    gamma+=d.getVector().get(str);
        }
        return alpha+0.75*beta+0.25*gamma;
    }
}
