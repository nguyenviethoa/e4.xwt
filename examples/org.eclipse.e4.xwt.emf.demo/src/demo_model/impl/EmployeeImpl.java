/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package demo_model.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import demo_model.Address;
import demo_model.Company;
import demo_model.Demo_modelPackage;
import demo_model.Employee;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Employee</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link demo_model.impl.EmployeeImpl#getFirstname <em>Firstname</em>}</li>
 *   <li>{@link demo_model.impl.EmployeeImpl#getCompany <em>Company</em>}</li>
 *   <li>{@link demo_model.impl.EmployeeImpl#getLastname <em>Lastname</em>}</li>
 *   <li>{@link demo_model.impl.EmployeeImpl#getAddress <em>Address</em>}</li>
 *   <li>{@link demo_model.impl.EmployeeImpl#getPosition <em>Position</em>}</li>
 *   <li>{@link demo_model.impl.EmployeeImpl#getEmail <em>Email</em>}</li>
 *   <li>{@link demo_model.impl.EmployeeImpl#getPhone <em>Phone</em>}</li>
 *   <li>{@link demo_model.impl.EmployeeImpl#getBirthday <em>Birthday</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EmployeeImpl extends EObjectImpl implements Employee {
	/**
	 * The default value of the '{@link #getFirstname() <em>Firstname</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getFirstname()
	 * @generated
	 * @ordered
	 */
	protected static final String FIRSTNAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFirstname() <em>Firstname</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getFirstname()
	 * @generated
	 * @ordered
	 */
	protected String firstname = FIRSTNAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getLastname() <em>Lastname</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getLastname()
	 * @generated
	 * @ordered
	 */
	protected static final String LASTNAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLastname() <em>Lastname</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getLastname()
	 * @generated
	 * @ordered
	 */
	protected String lastname = LASTNAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAddress() <em>Address</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getAddress()
	 * @generated
	 * @ordered
	 */
	protected Address address;

	/**
	 * The default value of the '{@link #getPosition() <em>Position</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getPosition()
	 * @generated
	 * @ordered
	 */
	protected static final String POSITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPosition() <em>Position</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getPosition()
	 * @generated
	 * @ordered
	 */
	protected String position = POSITION_EDEFAULT;

	/**
	 * The default value of the '{@link #getEmail() <em>Email</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getEmail()
	 * @generated
	 * @ordered
	 */
	protected static final String EMAIL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEmail() <em>Email</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getEmail()
	 * @generated
	 * @ordered
	 */
	protected String email = EMAIL_EDEFAULT;

	/**
	 * The default value of the '{@link #getPhone() <em>Phone</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getPhone()
	 * @generated
	 * @ordered
	 */
	protected static final String PHONE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPhone() <em>Phone</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getPhone()
	 * @generated
	 * @ordered
	 */
	protected String phone = PHONE_EDEFAULT;

	/**
	 * The default value of the '{@link #getBirthday() <em>Birthday</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getBirthday()
	 * @generated
	 * @ordered
	 */
	protected static final Date BIRTHDAY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBirthday() <em>Birthday</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getBirthday()
	 * @generated
	 * @ordered
	 */
	protected Date birthday = BIRTHDAY_EDEFAULT;

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EmployeeImpl() {
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
		return Demo_modelPackage.Literals.EMPLOYEE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated not
	 */
	public void setFirstname(String newFirstname) {
		String oldFirstname = firstname;
		firstname = newFirstname;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.EMPLOYEE__FIRSTNAME, oldFirstname, firstname));
		changeSupport.firePropertyChange("firstname", oldFirstname, firstname);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Company getCompany() {
		if (eContainerFeatureID != Demo_modelPackage.EMPLOYEE__COMPANY) return null;
		return (Company)eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCompany(Company newCompany, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newCompany, Demo_modelPackage.EMPLOYEE__COMPANY, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setCompany(Company newCompany) {
		if (newCompany != eInternalContainer() || (eContainerFeatureID != Demo_modelPackage.EMPLOYEE__COMPANY && newCompany != null)) {
			if (EcoreUtil.isAncestor(this, newCompany))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newCompany != null)
				msgs = ((InternalEObject)newCompany).eInverseAdd(this, Demo_modelPackage.COMPANY__EMPLOYEES, Company.class, msgs);
			msgs = basicSetCompany(newCompany, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.EMPLOYEE__COMPANY, newCompany, newCompany));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated not
	 */
	public void setLastname(String newLastname) {
		String oldLastname = lastname;
		lastname = newLastname;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.EMPLOYEE__LASTNAME, oldLastname, lastname));
		changeSupport.firePropertyChange("lastname", oldLastname, lastname);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAddress(Address newAddress, NotificationChain msgs) {
		Address oldAddress = address;
		address = newAddress;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Demo_modelPackage.EMPLOYEE__ADDRESS, oldAddress, newAddress);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setAddress(Address newAddress) {
		if (newAddress != address) {
			NotificationChain msgs = null;
			if (address != null)
				msgs = ((InternalEObject)address).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - Demo_modelPackage.EMPLOYEE__ADDRESS, null, msgs);
			if (newAddress != null)
				msgs = ((InternalEObject)newAddress).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - Demo_modelPackage.EMPLOYEE__ADDRESS, null, msgs);
			msgs = basicSetAddress(newAddress, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.EMPLOYEE__ADDRESS, newAddress, newAddress));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setPosition(String newPosition) {
		String oldPosition = position;
		position = newPosition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.EMPLOYEE__POSITION, oldPosition, position));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated not
	 */
	public void setEmail(String newEmail) {
		String oldEmail = email;
		email = newEmail;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.EMPLOYEE__EMAIL, oldEmail, email));
		changeSupport.firePropertyChange("email", oldEmail, email);

	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated not
	 */
	public void setPhone(String newPhone) {
		String oldPhone = phone;
		phone = newPhone;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.EMPLOYEE__PHONE, oldPhone, phone));
		changeSupport.firePropertyChange("phone", oldPhone, phone);

	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Date getBirthday() {
		return birthday;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated not
	 */
	public void setBirthday(Date newBirthday) {
		Date oldBirthday = birthday;
		birthday = newBirthday;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, Demo_modelPackage.EMPLOYEE__BIRTHDAY, oldBirthday, birthday));
		changeSupport.firePropertyChange("birthday", oldBirthday, birthday);

	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case Demo_modelPackage.EMPLOYEE__COMPANY:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetCompany((Company)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case Demo_modelPackage.EMPLOYEE__COMPANY:
				return basicSetCompany(null, msgs);
			case Demo_modelPackage.EMPLOYEE__ADDRESS:
				return basicSetAddress(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID) {
			case Demo_modelPackage.EMPLOYEE__COMPANY:
				return eInternalContainer().eInverseRemove(this, Demo_modelPackage.COMPANY__EMPLOYEES, Company.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case Demo_modelPackage.EMPLOYEE__FIRSTNAME:
				return getFirstname();
			case Demo_modelPackage.EMPLOYEE__COMPANY:
				return getCompany();
			case Demo_modelPackage.EMPLOYEE__LASTNAME:
				return getLastname();
			case Demo_modelPackage.EMPLOYEE__ADDRESS:
				return getAddress();
			case Demo_modelPackage.EMPLOYEE__POSITION:
				return getPosition();
			case Demo_modelPackage.EMPLOYEE__EMAIL:
				return getEmail();
			case Demo_modelPackage.EMPLOYEE__PHONE:
				return getPhone();
			case Demo_modelPackage.EMPLOYEE__BIRTHDAY:
				return getBirthday();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case Demo_modelPackage.EMPLOYEE__FIRSTNAME:
				setFirstname((String)newValue);
				return;
			case Demo_modelPackage.EMPLOYEE__COMPANY:
				setCompany((Company)newValue);
				return;
			case Demo_modelPackage.EMPLOYEE__LASTNAME:
				setLastname((String)newValue);
				return;
			case Demo_modelPackage.EMPLOYEE__ADDRESS:
				setAddress((Address)newValue);
				return;
			case Demo_modelPackage.EMPLOYEE__POSITION:
				setPosition((String)newValue);
				return;
			case Demo_modelPackage.EMPLOYEE__EMAIL:
				setEmail((String)newValue);
				return;
			case Demo_modelPackage.EMPLOYEE__PHONE:
				setPhone((String)newValue);
				return;
			case Demo_modelPackage.EMPLOYEE__BIRTHDAY:
				setBirthday((Date)newValue);
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
			case Demo_modelPackage.EMPLOYEE__FIRSTNAME:
				setFirstname(FIRSTNAME_EDEFAULT);
				return;
			case Demo_modelPackage.EMPLOYEE__COMPANY:
				setCompany((Company)null);
				return;
			case Demo_modelPackage.EMPLOYEE__LASTNAME:
				setLastname(LASTNAME_EDEFAULT);
				return;
			case Demo_modelPackage.EMPLOYEE__ADDRESS:
				setAddress((Address)null);
				return;
			case Demo_modelPackage.EMPLOYEE__POSITION:
				setPosition(POSITION_EDEFAULT);
				return;
			case Demo_modelPackage.EMPLOYEE__EMAIL:
				setEmail(EMAIL_EDEFAULT);
				return;
			case Demo_modelPackage.EMPLOYEE__PHONE:
				setPhone(PHONE_EDEFAULT);
				return;
			case Demo_modelPackage.EMPLOYEE__BIRTHDAY:
				setBirthday(BIRTHDAY_EDEFAULT);
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
			case Demo_modelPackage.EMPLOYEE__FIRSTNAME:
				return FIRSTNAME_EDEFAULT == null ? firstname != null : !FIRSTNAME_EDEFAULT.equals(firstname);
			case Demo_modelPackage.EMPLOYEE__COMPANY:
				return getCompany() != null;
			case Demo_modelPackage.EMPLOYEE__LASTNAME:
				return LASTNAME_EDEFAULT == null ? lastname != null : !LASTNAME_EDEFAULT.equals(lastname);
			case Demo_modelPackage.EMPLOYEE__ADDRESS:
				return address != null;
			case Demo_modelPackage.EMPLOYEE__POSITION:
				return POSITION_EDEFAULT == null ? position != null : !POSITION_EDEFAULT.equals(position);
			case Demo_modelPackage.EMPLOYEE__EMAIL:
				return EMAIL_EDEFAULT == null ? email != null : !EMAIL_EDEFAULT.equals(email);
			case Demo_modelPackage.EMPLOYEE__PHONE:
				return PHONE_EDEFAULT == null ? phone != null : !PHONE_EDEFAULT.equals(phone);
			case Demo_modelPackage.EMPLOYEE__BIRTHDAY:
				return BIRTHDAY_EDEFAULT == null ? birthday != null : !BIRTHDAY_EDEFAULT.equals(birthday);
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
		result.append(" (firstname: ");
		result.append(firstname);
		result.append(", lastname: ");
		result.append(lastname);
		result.append(", position: ");
		result.append(position);
		result.append(", email: ");
		result.append(email);
		result.append(", phone: ");
		result.append(phone);
		result.append(", birthday: ");
		result.append(birthday);
		result.append(')');
		return result.toString();
	}

} // EmployeeImpl
