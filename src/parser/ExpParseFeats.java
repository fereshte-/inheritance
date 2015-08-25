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



package parser;

import java.util.*;
import lambda.*;

public class ExpParseFeats implements ParseFeatureSet {

	static boolean share = true;

	public void setFeats(Cell c, List<Cell> children, HashVector feats){
		if (c.getStart()!=0 || c.getEnd()!=Globals.lastWordIndex) 
			return;
		setFeats(c.getCat().getSem(),feats);
	}

	public void setFeats(Exp sem, HashVector feats){
		String index = "RN:"+(sem.inferType()!=null);
		feats.set(index,feats.get(index)+scale);
		boolean first = true;
		double sc = 0.5;
		for (Exp e : sem.allSubExps()){
			sc = 0.5;
			if (e instanceof Lit){
				Lit l = (Lit)e;
				String head = l.getHeadString();
				for (int i=0; i<l.arity(); i++){
					// use cache from inferType() call above for type info
					index = "LHT:"+i+":"+head+":"+l.getArg(i).inferedType;
					feats.set(index,feats.get(index)+scale * sc);

					index = "LHH:"+i+":"+head+":"+l.getArg(i).getHeadString();
					feats.set(index,feats.get(index)+scale * sc);

					sc*=0.5;
					if(share && l.getPred().getParent() !=null){
						String parent = l.getPred().getParent();
						index = "LHTP1:"+i+":"+parent+":"+l.getArg(i).inferedType;
						feats.set(index,feats.get(index)+scale * sc);

						index = "LHHP1:"+i+":"+parent+":"+l.getArg(i).getHeadString();
						feats.set(index,feats.get(index)+scale * sc);
					}
					if(share && l.getArg(i) instanceof Lit && ((Lit) l.getArg(i)).getPred().getParent()!=null){
						String child_parent = ((Lit) l.getArg(i)).getPred().getParent();

						index = "LHHP2:"+i+":"+head+":"+child_parent;
						feats.set(index,feats.get(index)+scale * sc);

						if(l.getPred().getParent() !=null){
							String parent = l.getPred().getParent();
							index = "LHHP12:"+i+":"+parent+":"+child_parent;
							feats.set(index,feats.get(index)+scale *sc * 0.5);
						}
					}
				}
				if(share && first){
					index = "LPT:"+":"+"first"+":"+l.inferedType;
					feats.set(index,feats.get(index)+scale * sc);

					index = "LPH:"+":"+"first"+":"+l.getHeadString();
					feats.set(index,feats.get(index)+scale * sc);

					if(l.getPred().getParent() != null){
						index = "LPHP:"+":"+"first"+":"+l.getPred().getParent();
						feats.set(index,feats.get(index)+scale * sc);
					}
					first = false;
				}
			}
			if(e instanceof BoolBoolOps){
				for (String id : ((BoolBoolOps)e).getHeadPairs()){
					index = "BB:"+id;
					feats.set(index,feats.get(index)+scale * sc);
				}
			}
		}
	}

	public double score(Cell c, List<Cell> children, HashVector theta){
		if (c.getStart()!=0 || c.getEnd()!=Globals.lastWordIndex) 
			return 0.0;
		double score = 0.0;
		Exp sem = c.getCat().getSem();

		String index = "RN:"+(sem.inferType()!=null);
		score+=theta.get(index)*scale;

		boolean first = true;
		double sc = 0.5;
		for (Exp e : sem.allSubExps()){	
			sc = 0.5;
			if (e instanceof Lit){
				Lit l = (Lit)e;
				String head = l.getHeadString();
				for (int i=0; i<l.arity(); i++){
					// use cache from inferType() call above for type info
					index = "LHT:"+i+":"+head+":"+l.getArg(i).inferedType;
					score+=theta.get(index)*scale * sc;

					index = "LHH:"+i+":"+head+":"+l.getArg(i).getHeadString();
					score+=theta.get(index)*scale * sc;

					sc *= 0.5;
					if(share && l.getPred().getParent() !=null){
						String parent = l.getPred().getParent();
						index = "LHTP1:"+i+":"+parent+":"+l.getArg(i).inferedType;
						score+=theta.get(index)*scale * sc;

						index = "LHHP1:"+i+":"+parent+":"+l.getArg(i).getHeadString();
						score+=theta.get(index)*scale * sc;
					}
					if(share && l.getArg(i) instanceof Lit && ((Lit) l.getArg(i)).getPred().getParent()!=null){
						String child_parent = ((Lit) l.getArg(i)).getPred().getParent();

						index = "LHHP2:"+i+":"+head+":"+child_parent;
						score+=theta.get(index)*scale * sc;

						if(l.getPred().getParent() !=null){
							String parent = l.getPred().getParent();
							index = "LHHP12:"+i+":"+parent+":"+child_parent;
							score+=theta.get(index)*scale * sc * 0.5;
						}
					}
				}
				if(share && first){
					index = "LPT:"+":"+"first"+":"+l.inferedType;
					score+=theta.get(index)*scale * sc;

					index = "LPH:"+":"+"first"+":"+l.getHeadString();
					score+=theta.get(index)*scale * sc;

					if(l.getPred().getParent() != null){
						index = "LPHP:"+":"+"first"+":"+l.getPred().getParent();
						score+=theta.get(index)*scale * sc;
					}
					first = false;
				}
			}

			if(e instanceof BoolBoolOps){
				for (String id : ((BoolBoolOps)e).getHeadPairs()){
					index = "BB:"+id;
					score+=theta.get(index)*scale * sc;
				}
			}
		}

		return score;
	}

	public static void main(String[] args){
		PType.addTypesFromFile("../experiments/geo250-funql/geo-funql.types");
		Lang.loadLangFromFile("../experiments/geo250-funql/geo-funql.lang");

		ExpParseFeats epf = new ExpParseFeats();

		Exp e = Exp.makeExp("(answer (state (loc_1 (highest all:e))))");
		HashVector feats = new HashVector();

		epf.setFeats(e,feats);
		System.out.println(e);
		System.out.println(feats);

	}

	double scale=1.0;

}


///***************************  LICENSE  *******************************
// * This file is part of UBL.
// * 
// * UBL is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as 
// * published by the Free Software Foundation, either version 3 of the 
// * License, or (at your option) any later version.
// * 
// * UBL is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public 
// * License along with UBL.  If not, see <http://www.gnu.org/licenses/>.
// ***********************************************************************/
//
//
//
//package parser;
//
//import java.util.*;
//import lambda.*;
//
//public class ExpParseFeats implements ParseFeatureSet {
//
//	public static boolean share = f;
//
//	public void setFeats(Cell c, List<Cell> children, HashVector feats){
//		if (c.getStart()!=0 || c.getEnd()!=Globals.lastWordIndex) 
//			return;
//		setFeats(c.getCat().getSem(),feats);
//	}
//
//	public void setFeats(Exp sem, HashVector feats){
//
//		sem.inferType();
//		for(Exp e : sem.allSubExps()){
//			if(e instanceof Const || e.getExp()==null) continue;
//			String ename = e.getHeadString();
//			if(e instanceof ArgM && share)
//				ename = "ArgM";
//			for(int i=0; i<e.getExp().size(); i++){
//				Exp child = e.getExp().get(i);
//				if(child instanceof Var) continue;
//				String name = child.getHeadString();
//				if(child instanceof Const && share) 
//					name = child.type().toString();
//				if(child instanceof ArgM && share)
//					name = "ArgM";
//				String index = "LHH:"+i+":"+ename+":"+name;
//				feats.set(index,feats.get(index)+scale);
//				index = "LHT:"+i+":"+ename+":"+child.inferedType;
//				feats.set(index,feats.get(index)+scale);
//
//				if(e instanceof BoolBoolOps){
//					for(int j=i+1; j<e.getExp().size(); j++){
//						Exp child2=e.getExp().get(j);
//						if(child2 instanceof Var) continue;
//						String name2 = child2.getHeadString();
//						if(child2 instanceof Const && share) 
//							name2 = child.type().toString();
//						if(child2 instanceof ArgM && share)
//							name2 = "ArgM";
//						index = "LHB:"+i+":"+name+":"+name2;
//						feats.set(index,feats.get(index)+scale);
//
//						if(share && child instanceof Lit && ((Lit)child).getPred().getParent() !=null ){
//							String parent = ((Lit)child).getPred().getParent();
//							index = "LHBP:"+i+":"+parent+":"+name2;
//							feats.set(index,feats.get(index)+scale);
//
//							if(child2 instanceof Lit && ((Lit)child2).getPred().getParent() !=null ){
//								String parent2 = ((Lit)child2).getPred().getParent();
//								index = "LHBP:"+i+":"+parent+":"+parent2;
//								feats.set(index,feats.get(index)+scale);
//							}
//						}
//
//						if(share && child2 instanceof Lit && ((Lit)child2).getPred().getParent() !=null ){
//							String parent2 = ((Lit)child2).getPred().getParent();
//							index = "LHBP:"+i+":"+name+":"+parent2;
//							feats.set(index,feats.get(index)+scale);
//						}
//					}
//				}
//				if(share){
//					if(e instanceof Lit && ((Lit)e).getPred().getParent() !=null ){
//						String parent = ((Lit)e).getPred().getParent();
//						index = "LHP:"+i+":"+parent+":"+name;
//						feats.set(index,feats.get(index)+scale);
//						if(child instanceof Lit && ((Lit)child).getPred().getParent() !=null ){
//							String parent2 = ((Lit)child).getPred().getParent();
//							index = "LHP:"+i+":"+parent+":"+parent2;
//							feats.set(index,feats.get(index)+scale);
//						}
//					}
//
//					if(child instanceof Lit && ((Lit)child).getPred().getParent() !=null ){
//						String parent = ((Lit)child).getPred().getParent();
//						index = "LHP:"+i+":"+ename+":"+parent;
//						feats.set(index,feats.get(index)+scale);
//					}	
//				}
//			}
//		}
//
//		//		//System.out.println("feats: "+sem);
//		//
//		//		String index = "RN:"+(sem.inferType()!=null);
//		//		feats.set(index,feats.get(index)+scale);
//		//
//		//		//feats.set("AVG_DEPTH",sem.avgDepth()*scale);
//		//
//		//		for (Exp e : sem.allSubExps()){
//		//			if (e instanceof Lit){
//		//				Lit l = (Lit)e;
//		//				String head = l.getHeadString();
//		//				for (int i=0; i<l.arity(); i++){
//		//					// use cache from inferType() call above for type info
//		//					index = "LHT:"+i+":"+head+":"+l.getArg(i).inferedType;
//		//					feats.set(index,feats.get(index)+scale);
//		//
//		//
//		//					index = "LHH:"+i+":"+head+":"+l.getArg(i).getHeadString();
//		//					feats.set(index,feats.get(index)+scale);
//		//
//		//
//		//					String parent_head = l.getPred().getParent();
//		//					if(share && parent_head != null && !parent_head.equals("none")){
//		//						index = "LHPT:"+i+":"+parent_head+":"+l.getArg(i).inferedType;
//		//						feats.set(index,feats.get(index)+scale);
//		//
//		//
//		//						index = "LHPH:"+i+":"+parent_head+":"+l.getArg(i).getHeadString();
//		//						feats.set(index,feats.get(index)+scale);
//		//					}
//		//
//		//					Exp arg = l.getArg(i);
//		//					if(share && arg instanceof Lit){
//		//						Lit argLit = (Lit) arg;
//		//						//	System.out.println("fereshte + " + argLit.getPred().getParent());
//		//						if(argLit.getPred().getParent() != null){
//		//							index = "LHP:"+i+":"+head+":"+argLit.getPred().getParent();
//		//							//		System.out.println(" i added parent"  + index);
//		//							feats.set(index,feats.get(index)+ scale );
//		//						}
//		//					}
//		//				}
//		//			}
//		//			if(e instanceof BoolBoolOps){
//		//				for (String id : ((BoolBoolOps)e).getHeadPairs()){
//		//					index = "BB:"+id;
//		//					feats.set(index,feats.get(index)+scale);
//		//				}
//		//				for (String id : ((BoolBoolOps)e).getHeadPairsParents()){
//		//					index = "BB:"+id;
//		//					//		System.out.println(" i added boolbool "  + index);
//		//				if(share)	feats.set(index,feats.get(index)+ scale);
//		//				}
//		//			}
//		//			if(e instanceof ArgM){
//		//				ArgM argM = (ArgM) e;
//		//				Exp body = argM.getBody();
//		//				System.out.println("body is " + argM.getBody().toString());
//		//				if(body instanceof Lit){
//		//					Lit lbody = (Lit) body;
//		//					String p = lbody.getPred().getParent();
//		//					String id = argM.getHeadString() + ":";
//		//					if(p == null){
//		//						index = id + lbody.getHeadString();
//		//						//			System.out.println(" i added argm "  + index);
//		//
//		//						//	feats.set(index,feats.get(index)+scale);
//		//					}else{
//		//						index = id + p;
//		//						//			System.out.println(" i added argm "  + index);
//		//						//	feats.set(index,feats.get(index)+scale);
//		//					}
//		//				}
//		//			}
//		//		}
//	}
//
//
//
//	public double score(Cell c, List<Cell> children, HashVector theta){
//
//		if (c.getStart()!=0 || c.getEnd()!=Globals.lastWordIndex) 
//			return 0.0;
//		double score = 0.0;
//
//		Exp sem = c.getCat().getSem();
//		if (sem == null) return 0;
//		for(Exp e : sem.allSubExps()){
//			if(e instanceof Const || e.getExp()==null) continue;
//			String ename = e.getHeadString();
//			if(e instanceof ArgM && share)
//				ename = "ArgM";
//			for(int i=0; i<e.getExp().size(); i++){
//				Exp child = e.getExp().get(i);
//				if(child instanceof Var) continue;
//				String name = child.getHeadString();
//				if(child instanceof Const && share) 
//					name = child.type().toString();
//				if(child instanceof ArgM && share)
//					name = "ArgM";
//				String index = "LHH:"+i+":"+ename+":"+name;
//				score+=theta.get(index)*scale;
//				index = "LHT:"+i+":"+ename+":"+child.inferedType;
//				score+=theta.get(index)*scale;
//
//				if(e instanceof BoolBoolOps){
//					for(int j=i+1; j<e.getExp().size(); j++){
//						Exp child2=e.getExp().get(j);
//						if(child2 instanceof Var) continue;
//						String name2 = child2.getHeadString();
//						if(child2 instanceof Const && share) 
//							name2 = child.type().toString();
//						if(child2 instanceof ArgM && share)
//							name2 = "ArgM";
//						index = "LHB:"+i+":"+name+":"+name2;
//						score+=theta.get(index)*scale;
//
//						if(share && child instanceof Lit && ((Lit)child).getPred().getParent() !=null ){
//							String parent = ((Lit)child).getPred().getParent();
//							index = "LHBP:"+i+":"+parent+":"+name2;
//							score+=theta.get(index)*scale;
//
//							if(child2 instanceof Lit && ((Lit)child2).getPred().getParent() !=null ){
//								String parent2 = ((Lit)child2).getPred().getParent();
//								index = "LHBP:"+i+":"+parent+":"+parent2;
//								score+=theta.get(index)*scale;
//							}
//						}
//
//						if(share && child2 instanceof Lit && ((Lit)child2).getPred().getParent() !=null ){
//							String parent2 = ((Lit)child2).getPred().getParent();
//							index = "LHBP:"+i+":"+name+":"+parent2;
//							score+=theta.get(index)*scale;
//						}
//					}
//				}
//				if(share){
//					if(e instanceof Lit && ((Lit)e).getPred().getParent() !=null ){
//						String parent = ((Lit)e).getPred().getParent();
//						index = "LHP:"+i+":"+parent+":"+name;
//						score+=theta.get(index)*scale;
//						if(child instanceof Lit && ((Lit)child).getPred().getParent() !=null ){
//							String parent2 = ((Lit)child).getPred().getParent();
//							index = "LHP:"+i+":"+parent+":"+parent2;
//							score+=theta.get(index)*scale;
//						}
//					}
//
//					if(child instanceof Lit && ((Lit)child).getPred().getParent() !=null ){
//						String parent = ((Lit)child).getPred().getParent();
//						index = "LHP:"+i+":"+ename+":"+parent;
//						score+=theta.get(index)*scale;
//					}
//				}
//			}
//		}
//
//		//		Exp sem = c.getCat().getSem();
//		//
//		//		//System.out.println("score: "+c);
//		//
//		//		String index = "RN:"+(sem.inferType()!=null);
//		//		score+=theta.get(index)*scale;
//		//
//		//		//score+=theta.get("AVG_DEPTH")*scale*sem.avgDepth();
//		//
//		//		for (Exp e : sem.allSubExps()){
//		//			if (e instanceof Lit){
//		//				Lit l = (Lit)e;
//		//				String head = l.getHeadString();
//		//				for (int i=0; i<l.arity(); i++){
//		//					// use cache from inferType() call above for type info
//		//					index = "LHT:"+i+":"+head+":"+l.getArg(i).inferedType;
//		//					score+=theta.get(index)*scale;
//		//
//		//					index = "LHH:"+i+":"+head+":"+l.getArg(i).getHeadString();
//		//					score+=theta.get(index)*scale;
//		//					
//		//					String parent_head = l.getPred().getParent();
//		//					if(parent_head != null && !parent_head.equals("none")){
//		//						index = "LHPT:"+i+":"+parent_head+":"+l.getArg(i).inferedType;
//		//						score+=theta.get(index)*scale;
//		//
//		//
//		//						index = "LHPH:"+i+":"+parent_head+":"+l.getArg(i).getHeadString();
//		//						score+=theta.get(index)*scale;
//		//					}
//		//
//		//					Exp arg = l.getArg(i);
//		//					if(arg instanceof Lit){
//		//						Lit argLit = (Lit) arg;
//		//						if(argLit.getPred().getParent() != null){
//		//							index = "LHP:"+i+":"+head+":"+argLit.getPred().getParent();
//		//							score+=theta.get(index)*scale;
//		//						}
//		//					}
//		//				}
//		//				if(e instanceof BoolBoolOps){
//		//					for (String id : ((BoolBoolOps)e).getHeadPairs()){
//		//						index = "BB:"+id;
//		//						score+=theta.get(index)* scale;
//		//					}
//		//					for (String id : ((BoolBoolOps)e).getHeadPairsParents()){
//		//						index = "BB:"+id;
//		//						score+=theta.get(index)* scale;
//		//					}
//		//				}
//		//			}
//		//		}
//		return score;
//	}
//
//	public static void main(String[] args){
//		PType.addTypesFromFile("../experiments/geo250-funql/geo-funql.types");
//		Lang.loadLangFromFile("../experiments/geo250-funql/geo-funql.lang");
//
//		ExpParseFeats epf = new ExpParseFeats();
//
//		Exp e = Exp.makeExp("(answer (state (loc_1 (highest all:e))))");
//		HashVector feats = new HashVector();
//
//		epf.setFeats(e,feats);
//		System.out.println(e);
//		System.out.println(feats);
//
//	}
//
//	double scale=1.0;
//
//}