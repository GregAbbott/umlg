package org.tinker.interfacetest;

import com.tinkerpop.blueprints.pgm.Vertex;

import java.util.Set;
import java.util.UUID;

import org.tinker.concretetest.God;
import org.tuml.runtime.adaptor.GraphDb;
import org.tuml.runtime.adaptor.TinkerIdUtilFactory;
import org.tuml.runtime.adaptor.TransactionThreadEntityVar;
import org.tuml.runtime.collection.TinkerSet;
import org.tuml.runtime.collection.TinkerSetImpl;
import org.tuml.runtime.collection.TumlRuntimeProperty;
import org.tuml.runtime.domain.BaseTinker;
import org.tuml.runtime.domain.CompositionNode;
import org.tuml.runtime.domain.TinkerNode;

public class ManyA extends BaseTinker implements CompositionNode, IManyA {
	private TinkerSet<String> name;
	private TinkerSet<God> god;
	private TinkerSet<IMany> iMany;
	private TinkerSet<IManyB> iManyB;

	/** Constructor for ManyA
	 * 
	 * @param compositeOwner 
	 */
	public ManyA(God compositeOwner) {
		this.vertex = GraphDb.getDb().addVertex("dribble");
		createComponents();
		initialiseProperties();
		init(compositeOwner);
		TransactionThreadEntityVar.setNewEntity(this);
		defaultCreate();
	}
	
	/** Constructor for ManyA
	 * 
	 * @param vertex 
	 */
	public ManyA(Vertex vertex) {
		this.vertex=vertex;
		initialiseProperties();
	}
	
	/** Default constructor for ManyA
	 */
	public ManyA() {
	}
	
	/** Constructor for ManyA
	 * 
	 * @param persistent 
	 */
	public ManyA(Boolean persistent) {
		this.vertex = GraphDb.getDb().addVertex("dribble");
		TransactionThreadEntityVar.setNewEntity(this);
		defaultCreate();
		initialiseProperties();
	}

	public void addToGod(God god) {
		if ( god != null ) {
			this.god.add(god);
		}
	}
	
	public void addToIMany(IMany iMany) {
		if ( iMany != null ) {
			this.iMany.add(iMany);
		}
	}
	
	public void addToIMany(Set<IMany> iMany) {
		if ( !iMany.isEmpty() ) {
			this.iMany.addAll(iMany);
		}
	}
	
	public void addToIManyB(IManyB iManyB) {
		if ( iManyB != null ) {
			this.iManyB.add(iManyB);
		}
	}
	
	public void addToIManyB(Set<IManyB> iManyB) {
		if ( !iManyB.isEmpty() ) {
			this.iManyB.addAll(iManyB);
		}
	}
	
	public void addToName(String name) {
		if ( name != null ) {
			this.name.add(name);
		}
	}
	
	public void clearGod() {
		this.god.clear();
	}
	
	public void clearIMany() {
		this.iMany.clear();
	}
	
	public void clearIManyB() {
		this.iManyB.clear();
	}
	
	public void clearName() {
		this.name.clear();
	}
	
	public void createComponents() {
	}
	
	@Override
	public void delete() {
	}
	
	public God getGod() {
		TinkerSet<God> tmp = this.god;
		if ( !tmp.isEmpty() ) {
			return tmp.iterator().next();
		} else {
			return null;
		}
	}
	
	public TinkerSet<IMany> getIMany() {
		return this.iMany;
	}
	
	public TinkerSet<IManyB> getIManyB() {
		return this.iManyB;
	}
	
	@Override
	public Long getId() {
		return TinkerIdUtilFactory.getIdUtil().getId(this.vertex);
	}
	
	public String getName() {
		TinkerSet<String> tmp = this.name;
		if ( !tmp.isEmpty() ) {
			return tmp.iterator().next();
		} else {
			return null;
		}
	}
	
	@Override
	public int getObjectVersion() {
		return TinkerIdUtilFactory.getIdUtil().getVersion(this.vertex);
	}
	
	@Override
	public TinkerNode getOwningObject() {
		return getGod();
	}
	
	@Override
	public String getUid() {
		String uid = (String) this.vertex.getProperty("uid");
		if ( uid==null || uid.trim().length()==0 ) {
			uid=UUID.randomUUID().toString();
			this.vertex.setProperty("uid", uid);
		}
		return uid;
	}
	
	/** This gets called on creation with the compositional owner. The composition owner does not itself need to be a composite node
	 * 
	 * @param compositeOwner 
	 */
	@Override
	public void init(TinkerNode compositeOwner) {
		this.god.add((God)compositeOwner);
		this.hasInitBeenCalled = true;
		initVariables();
	}
	
	public void initVariables() {
	}
	
	@Override
	public void initialiseProperties() {
		this.name =  new TinkerSetImpl<String>(this, ManyARuntimePropertyEnum.name);
		this.god =  new TinkerSetImpl<God>(this, ManyARuntimePropertyEnum.god);
		this.iMany =  new TinkerSetImpl<IMany>(this, ManyARuntimePropertyEnum.iMany);
		this.iManyB =  new TinkerSetImpl<IManyB>(this, ManyARuntimePropertyEnum.iManyB);
	}
	
	@Override
	public void initialiseProperty(TumlRuntimeProperty tumlRuntimeProperty) {
		switch ( (ManyARuntimePropertyEnum.fromLabel(tumlRuntimeProperty.getLabel())) ) {
			case iManyB:
				this.iManyB =  new TinkerSetImpl<IManyB>(this, ManyARuntimePropertyEnum.iManyB);
			break;
		
			case iMany:
				this.iMany =  new TinkerSetImpl<IMany>(this, ManyARuntimePropertyEnum.iMany);
			break;
		
			case god:
				this.god =  new TinkerSetImpl<God>(this, ManyARuntimePropertyEnum.god);
			break;
		
			case name:
				this.name =  new TinkerSetImpl<String>(this, ManyARuntimePropertyEnum.name);
			break;
		
		}
	}
	
	@Override
	public boolean isTinkerRoot() {
		return false;
	}
	
	public void removeFromGod(God god) {
		if ( god != null ) {
			this.god.remove(god);
		}
	}
	
	public void removeFromGod(Set<God> god) {
		if ( !god.isEmpty() ) {
			this.god.removeAll(god);
		}
	}
	
	public void removeFromIMany(IMany iMany) {
		if ( iMany != null ) {
			this.iMany.remove(iMany);
		}
	}
	
	public void removeFromIMany(Set<IMany> iMany) {
		if ( !iMany.isEmpty() ) {
			this.iMany.removeAll(iMany);
		}
	}
	
	public void removeFromIManyB(IManyB iManyB) {
		if ( iManyB != null ) {
			this.iManyB.remove(iManyB);
		}
	}
	
	public void removeFromIManyB(Set<IManyB> iManyB) {
		if ( !iManyB.isEmpty() ) {
			this.iManyB.removeAll(iManyB);
		}
	}
	
	public void removeFromName(Set<String> name) {
		if ( !name.isEmpty() ) {
			this.name.removeAll(name);
		}
	}
	
	public void removeFromName(String name) {
		if ( name != null ) {
			this.name.remove(name);
		}
	}
	
	public void setGod(God god) {
		clearGod();
		addToGod(god);
	}
	
	public void setIMany(Set<IMany> iMany) {
		clearIMany();
		addToIMany(iMany);
	}
	
	public void setIManyB(Set<IManyB> iManyB) {
		clearIManyB();
		addToIManyB(iManyB);
	}
	
	@Override
	public void setId(Long id) {
		TinkerIdUtilFactory.getIdUtil().setId(this.vertex, id);
	}
	
	public void setName(String name) {
		clearName();
		addToName(name);
	}

	public enum ManyARuntimePropertyEnum implements TumlRuntimeProperty {
		name(true,false,"org__tinker__interfacetest__IMany__name",false,false,true,false,1,1),
		god(false,false,"A_<god>_<iMany>",false,false,true,false,1,1),
		iMany(true,true,"A_<god>_<iMany>",false,true,false,false,-1,0),
		iManyB(false,false,"A_<iManyA>_<iManyB>",false,false,false,true,-1,0);
		private boolean controllingSide;
		private boolean composite;
		private String label;
		private boolean oneToOne;
		private boolean oneToMany;
		private boolean manyToOne;
		private boolean manyToMany;
		private int upper;
		private int lower;
		/** Constructor for ManyARuntimePropertyEnum
		 * 
		 * @param controllingSide 
		 * @param composite 
		 * @param label 
		 * @param oneToOne 
		 * @param oneToMany 
		 * @param manyToOne 
		 * @param manyToMany 
		 * @param upper 
		 * @param lower 
		 */
		private ManyARuntimePropertyEnum(boolean controllingSide, boolean composite, String label, boolean oneToOne, boolean oneToMany, boolean manyToOne, boolean manyToMany, int upper, int lower) {
			this.controllingSide = controllingSide;
			this.composite = composite;
			this.label = label;
			this.oneToOne = oneToOne;
			this.oneToMany = oneToMany;
			this.manyToOne = manyToOne;
			this.manyToMany = manyToMany;
			this.upper = upper;
			this.lower = lower;
		}
	
		static public ManyARuntimePropertyEnum fromLabel(String label) {
			if ( name.getLabel().equals(label) ) {
				return name;
			}
			if ( god.getLabel().equals(label) ) {
				return god;
			}
			if ( iMany.getLabel().equals(label) ) {
				return iMany;
			}
			if ( iManyB.getLabel().equals(label) ) {
				return iManyB;
			}
			throw new IllegalStateException();
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public int getLower() {
			return this.lower;
		}
		
		public int getUpper() {
			return this.upper;
		}
		
		public boolean isComposite() {
			return this.composite;
		}
		
		public boolean isControllingSide() {
			return this.controllingSide;
		}
		
		public boolean isManyToMany() {
			return this.manyToMany;
		}
		
		public boolean isManyToOne() {
			return this.manyToOne;
		}
		
		public boolean isOneToMany() {
			return this.oneToMany;
		}
		
		public boolean isOneToOne() {
			return this.oneToOne;
		}
		
		@Override
		public boolean isValid(int elementCount) {
			return (getUpper() == -1 || elementCount <= getUpper()) && elementCount >= getLower();
		}
	
	}
}