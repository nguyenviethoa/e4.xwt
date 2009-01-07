/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package demo_model.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import demo_model.Address;
import demo_model.Demo_modelPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Address</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link demo_model.impl.AddressImpl#getCity <em>City</em>}</li>
 *   <li>{@link demo_model.impl.AddressImpl#getStreet <em>Street</em>}</li>
 *   <li>{@link demo_model.impl.AddressImpl#getZipcode <em>Zipcode</em>}</li>
 *   <li>{@link demo_model.impl.AddressImpl#getState <em>State</em>}</li>
 *   <li>{@link demo_model.impl.AddressImpl#getCountry <em>Country</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AddressImpl extends EObjectImpl implements Address {
	/**
	 * The default value of the '{@link #getCity() <em>City</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getCity()
	 * @generated
	 * @ordered
	 */
	protected static final String CITY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCity() <em>City</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getCity()
	 * @generated
	 * @ordered
	 */
	protected String city = CITY_EDEFAULT;

	/**
	 * The default value of the '{@link #getStreet() <em>Street</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getStreet()
	 * @generated
	 * @ordered
	 */
	protected static final String STREET_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getStreet() <em>Street</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getStreet()
	 * @generated
	 * @ordered
	 */
	protected String street = STREET_EDEFAULT;

	/**
	 * The default value of the '{@link #getZipcode() <em>Zipcode</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getZipcode()
	 * @generated
	 * @ordered
	 */
	protected static final int ZIPCODE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getZipcode() <em>Zipcode</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getZipcode()
	 * @generated
	 * @ordered
	 */
	protected int zipcode = ZIPCODE_EDEFAULT;

	/**
	 * The default value of the '{@link #getState() <em>State</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected static final String STATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getState() <em>State</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getState()
	 * @generated
	 * @ordered
	 */
	protected String state = STATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getCountry() <em>Country</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getCountry()
	 * @generated
	 * @ordered
	 */
	protected static final String COUNTRY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCountry() <em>Country</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getCountry()
	 * @generated
	 * @ordered
	 */
	protected String country = COUNTRY_EDEFAULT;

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected AddressImpl() {
		super();
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Demo_modelPackage.Literals.ADDRESS;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getCity() {
		return city;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated not
	 */
	public void setCity(String newCity) {
		String oldCity = city;
		city = newCity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.ADDRESS__CITY, oldCity, city));
		changeSupport.firePropertyChange("city", oldCity, city);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated not
	 */
	public void setStreet(String newStreet) {
		String oldStreet = street;
		street = newStreet;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.ADDRESS__STREET, oldStreet, street));
		changeSupport.firePropertyChange("street", oldStreet, street);

	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public int getZipcode() {
		return zipcode;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated not
	 */
	public void setZipcode(int newZipcode) {
		int oldZipcode = zipcode;
		zipcode = newZipcode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.ADDRESS__ZIPCODE, oldZipcode, zipcode));
		changeSupport.firePropertyChange("zipcode", oldZipcode, zipcode);

	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getState() {
		return state;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated not
	 */
	public void setState(String newState) {
		String oldState = state;
		state = newState;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.ADDRESS__STATE, oldState, state));
		changeSupport.firePropertyChange("state", oldState, state);

	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated not
	 */
	public void setCountry(String newCountry) {
		String oldCountry = country;
		country = newCountry;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.ADDRESS__COUNTRY, oldCountry, country));
		changeSupport.firePropertyChange("country", oldCountry, country);

	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case Demo_modelPackage.ADDRESS__CITY:
				return getCity();
			case Demo_modelPackage.ADDRESS__STREET:
				return getStreet();
			case Demo_modelPackage.ADDRESS__ZIPCODE:
				return new Integer(getZipcode());
			case Demo_modelPackage.ADDRESS__STATE:
				return getState();
			case Demo_modelPackage.ADDRESS__COUNTRY:
				return getCountry();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case Demo_modelPackage.ADDRESS__CITY:
				setCity((String)newValue);
				return;
			case Demo_modelPackage.ADDRESS__STREET:
				setStreet((String)newValue);
				return;
			case Demo_modelPackage.ADDRESS__ZIPCODE:
				setZipcode(((Integer)newValue).intValue());
				return;
			case Demo_modelPackage.ADDRESS__STATE:
				setState((String)newValue);
				return;
			case Demo_modelPackage.ADDRESS__COUNTRY:
				setCountry((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case Demo_modelPackage.ADDRESS__CITY:
				setCity(CITY_EDEFAULT);
				return;
			case Demo_modelPackage.ADDRESS__STREET:
				setStreet(STREET_EDEFAULT);
				return;
			case Demo_modelPackage.ADDRESS__ZIPCODE:
				setZipcode(ZIPCODE_EDEFAULT);
				return;
			case Demo_modelPackage.ADDRESS__STATE:
				setState(STATE_EDEFAULT);
				return;
			case Demo_modelPackage.ADDRESS__COUNTRY:
				setCountry(COUNTRY_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case Demo_modelPackage.ADDRESS__CITY:
				return CITY_EDEFAULT == null ? city != null : !CITY_EDEFAULT.equals(city);
			case Demo_modelPackage.ADDRESS__STREET:
				return STREET_EDEFAULT == null ? street != null : !STREET_EDEFAULT.equals(street);
			case Demo_modelPackage.ADDRESS__ZIPCODE:
				return zipcode != ZIPCODE_EDEFAULT;
			case Demo_modelPackage.ADDRESS__STATE:
				return STATE_EDEFAULT == null ? state != null : !STATE_EDEFAULT.equals(state);
			case Demo_modelPackage.ADDRESS__COUNTRY:
				return COUNTRY_EDEFAULT == null ? country != null : !COUNTRY_EDEFAULT.equals(country);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (city: ");
		result.append(city);
		result.append(", street: ");
		result.append(street);
		result.append(", zipcode: ");
		result.append(zipcode);
		result.append(", state: ");
		result.append(state);
		result.append(", country: ");
		result.append(country);
		result.append(')');
		return result.toString();
	}

} // AddressImpl
