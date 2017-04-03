package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import cop5556sp17.AST.Dec;

public class SymbolTable {

	// TODO add fields
	int current_scope, next_scope;
	Stack<Integer> scope_stack = new Stack<>();
	HashMap<String, HashMap<Integer, Dec>> symbHashMap = new HashMap<>();

	/**
	 * to be called when block entered
	 */
	public void enterScope() {
		// TODO: IMPLEMENT THIS
		current_scope = ++next_scope;
		scope_stack.push(current_scope);
	}

	/**
	 * leaves scope
	 */
	public void leaveScope() {
		// TODO: IMPLEMENT THIS
		scope_stack.pop();
		current_scope = scope_stack.peek();
	}

	public boolean insert(String ident, Dec dec) {
		// TODO: IMPLEMENT THIS
		HashMap<Integer, Dec> insMap = new HashMap<>();
		if(symbHashMap.get(ident)==null)
			insMap.put(current_scope, dec);
		else{
			insMap = symbHashMap.get(ident);
			if(insMap.get(current_scope)==null)
				insMap.put(current_scope, dec);
			else return false;
		}
		symbHashMap.put(ident, insMap);
		return true;
	}
	
	public Dec lookup(String ident) {
		// TODO: IMPLEMENT THIS
		// gets matching entry in hash table;
		// scan chain and return attributes for entry with
		// scope number closest to the top of the scope stack;
		Map<Integer, Dec> map = symbHashMap.get(ident);
		if(map==null || map.size()==0)
			return null;
		Dec dec = null;
		List<Integer> scopeList = new ArrayList<>();
		while(scope_stack.size()!=0){
			if(map.get(scope_stack.peek())!=null){
				dec = map.get(scope_stack.peek());
				if(scopeList.size()!=0)
					for(int i=scopeList.size()-1;i>=0;i--){
						scope_stack.push(scopeList.get(i));
					}
				break;
			}
			else{
				scopeList.add(scope_stack.pop());
			}
		}
		return dec;
	}

	public SymbolTable() {
		// TODO: IMPLEMENT THIS
		next_scope =0;
		current_scope =0;
		scope_stack.push(current_scope);
	}

	@Override
	public String toString() {
		// TODO: IMPLEMENT THIS
		StringBuilder sb = new StringBuilder();
		for (Entry<String, HashMap<Integer, Dec>> entry : symbHashMap.entrySet()) {
			Map<Integer, Dec> map = entry.getValue();
			sb.append(entry.getKey());
			sb.append(":");
			sb.append(map.toString());
		}
		return sb.toString();
	}

}
