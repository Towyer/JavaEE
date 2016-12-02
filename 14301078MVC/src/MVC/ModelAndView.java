package MVC;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModelAndView {

	private String viewName;
	private Map<String, Object> Map = new ConcurrentHashMap<String, Object>();
	private Obj objList[] = null;
	private int fieldNum = 0;
	private int ObjectNum = 0;
	
	public ModelAndView()
	{
		
	}
	
	public Object getMap(String name) {
		return Map.get(name);
	}
	
    public void addMap(String name, Object obj) {
		Map.put(name, obj);
		fieldNum++;
		objList = new Obj[fieldNum];
	}

	public void setViewName(String viewName1) {
		viewName = viewName1+".jsp";
	}
	
	public String getViewName() {
		return viewName;
	}

	public void addObject(String name, Object obj) {
		objList[ObjectNum] = new Obj();
		objList[ObjectNum].setName(name);
		objList[ObjectNum].setObj(obj);
		ObjectNum++;
	}
	
	public Obj[] getObjects() {
		return objList;
	}

}
