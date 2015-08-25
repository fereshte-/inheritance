/***************************  LICENSE  *******************************
 * This file is part of UBL.
 * 
 * UBL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * UBL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with UBL.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************/




package lambda;

import java.util.*;

import utils.*;

import java.io.*;

import com.google.common.collect.ArrayListMultimap;

/*
 * A lambda calc literal has a name, an arity, 
 * and a list of arguments that are Exps.  
 *
 * This class also represents function application
 * which should probably be seperate.
 */

public class Lit extends Exp {

	public Lit(String input, Map vars){
		LispReader lr = new LispReader(new StringReader(input));	
		String predName = lr.next();
		List argsList = new LinkedList();
		while (lr.hasNext()){
			argsList.add(Exp.makeExp(lr.next(),vars));
		}

		args = new Exp[argsList.size()];
		for (int i=0; i<args.length; i++){
			args[i]=(Exp)argsList.get(i);

		}
		pred = Lang.getPred(predName,args.length);
		if (pred==null){
			System.out.println("Lit: couldn't parse: "+input);
			System.exit(-1);
		}

		addToNameMap(Arrays.asList(args));
		//		System.err.println(this + "\n\n\n" + pred + " "+ pred.type() + " " + type());
	}

	private String reverse(String s){
		if(s.contains("_2"))
			return s.replace("_2", "");
		else
			return s+"_2";
	}

	@Override
	public String change(List varNames){
		return change(varNames, null);
	}

	public String change(List varNames, String st){
		StringBuffer result = new StringBuffer();
		String name = pred.getName().trim();
		if(name.equals("singleton") || name.equals("listValue") || name.equals("string") ||
				name.equals("ensureNumericEntity") || name.equals("ensureNumericProperty")){
			for (int i=0; i<args.length; i++){
				if (args[i]!=null)
					result.append(" ").append(args[i].change(varNames));
				else 
					result.append(" ").append("null");
			}
		}else if(name.equals("getProperty")){
			if(st==null){
				result.append("(").append(args[1].change(varNames));
				result.append(" ").append(args[0].change(varNames));
				result.append(")");
			}else{
				Lit l = (Lit)args[0];
				result.append("(").append(((Const)l.args[0]).name+":t");
				result.append(" " + st);
				result.append(")");
			}
		}else if(name.equals("superlative")){
			result.append("(").append(args[1].change(varNames).trim());

			result.append(" ").append(args[0].change(varNames));
			result.append(" ").append(args[2].change(varNames));
			result.append(")");
		}else if(name.equals("reverse")){
			result.append(args[0].change(varNames)+"_2");
		}else if(name.equals("filter")){
			if (args.length == 4){
				if(args[2].change(varNames).trim().equals("=")){
					result.append("(and ");
					result.append(args[0].change(varNames));
					result.append(" (").append(reverse(args[1].change(varNames)));
					result.append(" ").append(args[3].change(varNames));
					result.append(")");

					result.append(")");
				}else{
					result.append("(").append(args[2].change(varNames));
					result.append(" (").append(args[1].change(varNames));
					result.append(" ").append(args[0].change(varNames));
					result.append(")");

					result.append(" ").append(args[3].change(varNames));

					result.append(")");
				}
			}else if(args.length == 2){
				if(args[1].change(varNames).contains("won"))
						result.append("(and ").append(args[1].change(varNames)+":ar");
				else
					result.append("(and ").append(args[1].change(varNames)+":me");

				result.append(" ").append(args[0].change(varNames));
				result.append(")");
			}else{
				System.err.println("that is weird!");
			}

		}else if(name.equals("aggregate")){
			result.append("(").append(args[0].change(varNames));
			result.append(" ").append(args[1].change(varNames));
			result.append(")");
		}else if(name.equals("countComparative")){
			result.append(" (lambda $0 e ").append(" (and "+((Lit)args[0]).change(varNames, " $0"));
			result.append(" (").append(args[2].change(varNames));
			result.append(" (ccount (").append(args[1].change(varNames) + " $0))");
			result.append(" ").append(args[3].change(varNames));
			result.append("))");

		}else if(name.equals("countSuperlative")){
			//	if(args.length == 4){
			result.append("(").append("arg" + args[1].change(varNames).trim() + " $0");
			result.append(" ").append(args[0].change(varNames, "$0"));
			result.append(" (ccount");
			result.append(" (").append(args[2].change(varNames)).append(" $0)");
			result.append(")");
			result.append(")");
			//			}else{
			//
			//			}
		}else if(name.equals("time")){
			result.append(((Const)args[0]).name+":ti");
		}else if(name.equals("date") && args.length == 3){
			if(args[1].getHeadString().contains("jan")){
				result.append("(jan ");
				result.append(args[2]);
				result.append(")");
			}else{
				result.append(((Const)args[0]).name+":da");
			}
		}else if(name.equals("number") && args.length == 2){
			result.append("(hour ");
			result.append(args[0]);
			result.append(")");
		}else if(name.equals("number")){
			result.append(((Const)args[0]).name+":num");
		}else if(name.equals("concat")){
			result.append("(").append("or");
			for (int i=0; i<args.length; i++){
				if (args[i]!=null)
					result.append(" ").append(args[i].change(varNames));
				else 
					result.append(" ").append("null");
			}
			result.append(")");
		}else{
			result.append("(").append(pred.getName());
			for (int i=0; i<args.length; i++){
				if (args[i]!=null)
					result.append(" ").append(args[i].change(varNames));
				else 
					result.append(" ").append("null");
			}
			result.append(")");
		}
		return result.toString();
	}

	public List<Exp> getExp(){
		return Arrays.asList(args);
	}



	@Override
	public String getParent() {
		return getPred().getParent();
	}

	public Lit(Pred p, Exp e){
		args = new Exp[1];
		pred = p;
		args[0]=e;
	}

	public Lit(Pred p, Exp e0, Exp e1){
		args = new Exp[2];
		pred = p;
		args[0]=e0;
		args[1]=e1;
	}

	public Lit(Pred p, int arity){
		args = new Exp[arity];
		pred = p;
	}

	public Exp simplify(List<Var> vars){
		for (int i=0; i<args.length; i++){
			args[i]=args[i].simplify(vars);
		}
		return this;
	}

	public boolean functClash(Lit other){
		/*
	if (arity()==2 && other.arity()==2 &&
	    pred.equals(other.pred) &&
	    getArg(1) instanceof Const &&
	    other.getArg(1) instanceof Const &&
	    !pred.getName().equals("services"))
	    return true;
		 */
		if (arity()==1 && pred.equals(other.pred))
			return true;

		return false;
	}

	public Pred getPred(){
		return pred;
	}

	public void setArg(int i, Exp e){
		args[i]=e;
	}

	public Exp getArg(int i){
		return args[i];
	}

	public Exp replace(Exp olde, Exp newe){
		//System.out.println("Exp replace "+this);
		if (equals(olde)){
			return newe;
		}// else {
		//  System.out.println("NEQ : "+this+ " : "+olde);
		//}
		Exp e;
		for (int i=0; i<args.length; i++){
			if (args[i].equals(olde))
				args[i]=newe;
			else
				args[i]=args[i].replace(olde,newe);
			if (args[i]==null)
				return null;
		}
		return this;
	}

	public Exp instReplace(Exp olde, Exp newe){
		if (this==olde){
			return newe;
		}
		Exp e;
		for (int i=0; i<args.length; i++){
			args[i]=args[i].instReplace(olde,newe);
			if (args[i]==null)
				return null;
		}
		return this;
	}

	public Exp copy(){
		//System.out.println("Lit copy: "+this);
		Lit l = new Lit(pred, args.length);
		for (int i=0; i<args.length; i++){
			if (args[i]!=null)
				l.setArg(i,args[i].copy());
			else 
				System.out.println("NULL: "+this);
		}
		return l;
	}

	public boolean equals(Object o){
		if (o instanceof Lit){
			Lit l = (Lit) o;
			if (!pred.equals(l.pred)) return false;
			if (args.length!=l.args.length) return false;
			if(getHeadString().equals("=") || getHeadString().equals("!=")){
				if(args.length!=2)	return false;
				return (args[0].equals(l.args[0]) && args[1].equals(l.args[1])) ||
						(args[0].equals(l.args[1]) && args[1].equals(l.args[0]));
			}else{
				for (int i=0; i<args.length; i++){
					if (args[i]==null) {
						return l.args[i]==null;
					}
					if (!args[i].equals(l.args[i]))
						return false;
				}
				return true;
			}
		} else 
			return false;
	}

	public boolean equals(int type, Exp o){
		if (o instanceof Lit){
			Lit l = (Lit) o;
			if (!pred.equals(l.pred)) return false;
			if (args.length!=l.args.length) return false;
			for (int i=0; i<args.length; i++){
				if (type==NO_VARS && ((args[i] instanceof Var) ||
						(l.args[i] instanceof Var)))
					continue;
				if (!args[i].equals(type,l.args[i]))
					return false;
			}
			return true;
		} else return false;
	}

	public boolean equalsNoVars(Lit l){
		if (!pred.equals(l.pred)) return false;
		if (args.length!=l.args.length) return false;
		for (int i=0; i<args.length; i++){
			//if (!(args[i] instanceof Var) &&
			//!args[i].equals(l.args[i]))
			if ((args[i] instanceof Var) &&
					(l.args[i] instanceof Var))
				continue;
			if(!args[i].equals(l.args[i]))
				return false;
		}
		return true;
	}

	public int hashCode(){
		int ret = pred.hashCode();
		for (int i=0; i<args.length; i++)
			ret+=args[i].hashCode();
		return ret;
	}

	public double varPenalty(List varNames){
		double result = 0.0;
		for (int i=0; i<args.length; i++)
			result+=args[i].varPenalty(varNames);
		return result+varNames.size();
	}

	public String toString(List varNames){
		//	System.out.println("we are in tostring " + what + " " + to + " " + pred.getName() + " " + Exp.changeVersion);

		StringBuffer result = new StringBuffer();
		if(Exp.parentVersion && pred.getParent()!=null){
			result.append("(").append(pred.getParent());
		}else if(Exp.changeVersion && pred.getName().equals(what)){
			//	System.out.println("we are in change " + what + " " + to);
			result.append("(").append(to);
		}else{
			result.append("(").append(pred.getName());
		}
		for (int i=0; i<args.length; i++){
			if (args[i]!=null)
				result.append(" ").append(args[i].toString(varNames));
			else 
				result.append(" ").append("null");
		}
		result.append(")");
		return result.toString();
	}



	public Type inferType(List<Var> vars, List<List<Type>> varTypes){

		// first, find the compatible type signatures
		boolean foundVar = false;
		List<Type> inputTypes = new ArrayList<Type>(args.length);
		for (int i=0; i<args.length; i++){
			if (args[i] instanceof Var) foundVar = true;
			inputTypes.add(args[i].inferType(vars,varTypes));
		}

		List<List<Type>> typeSigs = pred.getTypeSigs();

		// find the type signatures that match the types so far
		List<List<Type>> matchedTuples = new LinkedList<List<Type>>();
		for (List<Type> tuple : typeSigs){
			boolean match = true;
			for (int i=0; match && i<args.length; i++){
				Type t= tuple.get(i);
				if (!t.matches(inputTypes.get(i))){
					match = false;
				}
			}
			if (match)
				matchedTuples.add(tuple);
		}
		if (matchedTuples.size()==0){
			//System.out.print("<");
			inferedType=null; // update cache
			return null;
		}
		//System.out.println("matchedTyples:"+matchedTuples);

		Set<List<Type>> newVarTypes = new HashSet<List<Type>>();
		// now, restrict the variable types 
		if (foundVar){
			Iterator<List<Type>> t = matchedTuples.iterator();
			while (t.hasNext()){
				List<Type> tuple = t.next();
				boolean foundMatch = false;
				for (List<Type> varTs : varTypes){
					boolean foundAll = true;
					List<Type> newTypes = new LinkedList<Type>();
					newTypes.addAll(varTs);
					for (int i=0; foundAll && i<args.length; i++){
						if (args[i] instanceof Var){
							Type st = tuple.get(i);		   
							Var v = (Var)args[i];
							int vi= vars.indexOf(v);	
							if (vi==-1) continue;
							Type vt = varTs.get(vi);
							if (vt.subType(st)) continue;
							if (st.subType(vt)){
								newTypes.set(vi,st);
							} else foundAll = false;
						}
					}
					if (foundAll){
						foundMatch = true;
						//System.out.println("new types: "+newTypes);
						newVarTypes.add(newTypes);
					}
				}
				if (!foundMatch) t.remove();
			}
			if (newVarTypes.size()==0 || matchedTuples.size()==0){
				//System.out.print("<");
				inferedType=null; // update cache
				return null;
			}
			varTypes.clear();
			varTypes.addAll(newVarTypes);
		}

		// compute the result type
		//System.out.println("matched: "+matchedTuples);
		Type rType=null;
		for (List<Type> tuple : matchedTuples){
			if (rType==null)
				rType = tuple.get(tuple.size()-1);
			else
				rType = rType.commonSuperType(tuple.get(tuple.size()-1));
			//  System.out.println(rType);
		}

		//System.out.print("<");
		//System.out.println("LIT: "+this+" --- type="+rType);

		boolean match = true;
		if(getHeadString().equals("=") ||
				getHeadString().equals("!=") ||
				getHeadString().equals("<") ||
				getHeadString().equals(">") ||
				getHeadString().equals("<=") ||
				getHeadString().equals(">=")){

			for(Exp e: args)
				if(!e.wellTyped())
					match= false;
			if(args.length !=2 ) 
				match= false;
			Exp left = args[0], right = args[1];

			Type t1=left.inferType(vars,varTypes);
			Type t2=right.inferType(vars,varTypes);
			if (t1==null || t2==null || !t1.matches(t2)){
				inferedType=null; // update cache
				return null;
			}
			//	rType = left.inferedType.commonSubType(right.inferedType);
		}
		inferedType=rType; // update cache
		return rType;
	}

	public Type type(){
		/*
	for (int i=0; i<args.length; i++)
	    if (args[i] == null || args[i].type()==null)
		return null;
		 */
		return pred.type();
	}

	public boolean wellTyped(){
		if(type()==null)
			return false;
		if(getHeadString().equals("=") ||
				getHeadString().equals("!=") ||
				getHeadString().equals("<") ||
				getHeadString().equals(">") ||
				getHeadString().equals("<=") ||
				getHeadString().equals(">=")){

			for(Exp e: args)
				if(!e.wellTyped())
					return false;
			if(args.length !=2 ) return false;
			Exp left = args[0], right = args[1];
			if (left instanceof Var && !((Var)left).updateTempType(right.type())){
				return false;
			}
			if (right instanceof Var && !((Var)right).updateTempType(left.type())){
				return false;
			}
			if (!left.type().matches(right.type())){
				return false;
			}
		}
		return true;
	}

	public void freeVars(List bound, List free){
		for (int i=0; i<args.length; i++){
			args[i].freeVars(bound,free);
		}
	}

	public void extractFuncts(List functors, List functees, Exp orig){
		for (int i=0; i<args.length; i++){
			List vars = args[i].freeVars();
			if (vars.size()==0){
				Var v = new Var(args[i].type());
				Exp c = args[i];
				args[i]=v;
				Exp functorbody = orig.copy();
				args[i]=c;
				Exp functor = new Funct(v,functorbody);
				functors.add(functor);
				functees.add(c);
			}
			args[i].extractFuncts(functors,functees,orig);
		}
	}

	public double complexity(){
		return 0.0;
	}

	public List merge(Exp e, Exp top){
		if (!(e instanceof Lit))
			return null;
		Lit l = (Lit)e;
		if (!pred.equals(l.pred))
			return null;
		int diffindex=-1;
		for (int i=0; i<args.length; i++){
			if (!args[i].equals(l.args[i])){
				if (diffindex==-1)
					diffindex = i;
				else
					return null;
			}
		}

		if (diffindex!=-1){
			List myFreeVars = args[diffindex].freeVars();
			List itsFreeVars = l.args[diffindex].freeVars();
			if (myFreeVars.size()==0 && itsFreeVars.size()==0){
				List result = new LinkedList();
				// we have the same function applied to two different 
				// const typed expressions
				Var v = new Var(args[diffindex].type());
				Exp c = args[diffindex];
				args[diffindex]=v;
				Exp functorbody = top.copy();
				args[diffindex]=c;
				Exp functor = new Funct(v,functorbody);
				result.add(functor);
				result.add(args[diffindex].copy());
				result.add(l.args[diffindex].copy());
				return result;
			}
		}
		return null;
	}

	public List merge(List exps, Exp top){
		/*
	// first make sure all Exps in e are Lits and that
	// they are all identical to me, except in one argument
	// position
	Exp e;
	Lit l;
	int diffindex=-1;
	Iterator i = exps.iterator();
	while (i.hasNext()){
	    e = (Exp)i.next();
	    if (!(e instanceof Lit)) return null;
	    l = (Lit)e;
	    if (!pred.equals(l.pred)) return null;
	    for (int j=0; j<args.length; j++){
		if (!args[j].equals(l.args[j])){
		    if (diffindex==-1)
			diffindex = j;
		    else if (diffindex!=j)
			return null;
		}
	    }
	}
	if (diffindex==-1) return null;

	// now do the merge but check to make sure
	// that each subexpression only has one free variable
	List result = new LinkedList();

	// first, make the functor and my functee
	List freeVars = args[diffindex].freeVars();
	if (freeVars.size()!=0) return null; // can't make functee
	Var v = new Var(args[diffindex].type());
	Exp c = args[diffindex];
	args[diffindex]=v;
	Exp functorbody = top.copy();
	args[diffindex]=c;
	Exp functor = new Funct(v,functorbody);
	result.add(functor);
	Type t = args[diffindex].type();
	result.add(args[diffindex].copy());

	// now, make all of the other functees if they 
	// have the correct type
	i = exps.iterator();
	while (i.hasNext()){
	    l = (Lit)i.next();
	    if (!l.args[diffindex].type().equals(t)) return null;
	    result.add(l.args[diffindex].copy());
	}
	return result;
		 */
		return null;
	}

	public void extractPTypeExps(List l){
		for (int i=0; i<args.length; i++){
			args[i].extractPTypeExps(l);
		}
	}

	public void allPreds(int arity, List result){
		if (arity==args.length || arity==-1)
			if (!result.contains(pred))
				result.add(pred);
		for (int i=0; i<args.length; i++){
			args[i].allPreds(arity,result);
		}
	}

	public void allLits(int arity, List result, boolean b){
		if (arity==args.length || arity==-1)
			if (!result.contains(this))
				result.add(this);
		for (int i=0; i<args.length; i++){
			args[i].allLits(arity,result,b);
		}
	}

	public void allSubExps(String type, List result){
		//if (pred.getClass().getName().equals(type))
		//result.add(pred);
		for (int i=0; i<args.length; i++){
			if (args[i].getClass().getName().equals(type))
				result.add(args[i]);
			args[i].allSubExps(type,result);
		}
	}

	public void allSubExps(Type type, List result){
		if (type==null) result.add(this);
		Type t = type();
		if (t!=null && t.equals(type)) result.add(this);
		for (int i=0; i<args.length; i++){
			args[i].allSubExps(type,result);
		}
	}

	public void allSubExps(List result){
		result.add(this);
		for (int i=0; i<args.length; i++){
			args[i].allSubExps(result);
		}
	}

	public void raisableSubExps(List<Exp> result){
		if (type().equals(PType.T))
			result.add(this);
	}

	public int predCount(Object p){
		int result = 0;
		if (pred.equals(p))
			result++;
		for (int i=0; i<args.length; i++){
			if (args[i]!=null)
				result+=args[i].predCount(p);
		}	
		return result;
	}

	public int repeatPredCount(int t, Object p){
		int result = 0;
		for (int i=0; i<args.length; i++){
			if (args[i]!=null)
				result+=args[i].repeatPredCount(t,p);
		}	
		return result;
	}

	public int expCount(int eq, Exp e){
		int count = 0;
		if (equals(eq,e)) count++;
		for (int i=0; i<args.length; i++){
			if (args[i]!=null)
				count+=args[i].expCount(eq,e);
		}	
		return count;
	}

	public int repeatExpCount(int t, Exp e){
		int result = 0;
		for (int i=0; i<args.length; i++){
			if (args[i]!=null)
				result+=args[i].repeatExpCount(t,e);
		}	
		return result;
	}

	public int expCount(int id){
		int result = 0;
		for (int i=0; i<args.length; i++)
			if (args[i]!=null)
				result+=args[i].expCount(id);
		return result;
	}

	public int arity(){
		return args.length;
	}

	public boolean removeUnscoped(List vars){
		for (int i=0; i<args.length; i++)
			if (args[i].removeUnscoped(vars))
				return true;
		return false;
	}

	public Exp deleteExp(Exp e){
		Lit l = new Lit(pred, args.length);
		for (int i=0; i<args.length; i++){
			if (args[i]!=null)
				l.setArg(i,args[i].deleteExp(e));
		}
		return l;
	}

	void getOuterRefs(Exp e, List<Exp> refs){	
		for (int i=0; i<args.length; i++)
			args[i].getOuterRefs(e,refs);
	}

	public void getConstStrings(List<String> result){
		result.add(pred.getName());
		for (int i=0; i<args.length; i++)
			args[i].getConstStrings(result);
	}

	public String getHeadString(){
		return pred.getName().trim();
	}

	public double avgDepth(int d){
		double total=d;
		for (int i=0; i<args.length; i++)
			total+=args[i].avgDepth(d+1);
		return total/(args.length+1);
	}

	Pred pred; 
	Exp[] args;

	// cache the return type
	Type retType = null;  

}
