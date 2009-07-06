/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MaxValueIndicator;
import org.talend.dataquality.indicators.MinValueIndicator;
import org.talend.dataquality.indicators.RangeIndicator;
import org.talend.utils.dates.DateUtils;
import org.talend.utils.dates.ElapsedTime;
import org.talend.utils.sql.Java2SqlType;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Range Indicator</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.talend.dataquality.indicators.impl.RangeIndicatorImpl#getLowerValue <em>Lower Value</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.RangeIndicatorImpl#getUpperValue <em>Upper Value</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.RangeIndicatorImpl#getDatatype <em>Datatype</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.impl.RangeIndicatorImpl#getRange <em>Range</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class RangeIndicatorImpl extends CompositeIndicatorImpl implements RangeIndicator {
    private static Logger log = Logger.getLogger(RangeIndicatorImpl.class);
    
    /**
     * The cached value of the '{@link #getLowerValue() <em>Lower Value</em>}' containment reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getLowerValue()
     * @generated
     * @ordered
     */
    protected MinValueIndicator lowerValue;

    /**
     * The cached value of the '{@link #getUpperValue() <em>Upper Value</em>}' containment reference. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getUpperValue()
     * @generated
     * @ordered
     */
    protected MaxValueIndicator upperValue;

    /**
     * The default value of the '{@link #getDatatype() <em>Datatype</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getDatatype()
     * @generated
     * @ordered
     */
    protected static final int DATATYPE_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getDatatype() <em>Datatype</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getDatatype()
     * @generated
     * @ordered
     */
    protected int datatype = DATATYPE_EDEFAULT;

    /**
     * The default value of the '{@link #getRange() <em>Range</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getRange()
     * @generated
     * @ordered
     */
    protected static final String RANGE_EDEFAULT = "";

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected RangeIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.RANGE_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MinValueIndicator getLowerValue() {
        return lowerValue;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetLowerValue(MinValueIndicator newLowerValue, NotificationChain msgs) {
        MinValueIndicator oldLowerValue = lowerValue;
        lowerValue = newLowerValue;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    IndicatorsPackage.RANGE_INDICATOR__LOWER_VALUE, oldLowerValue, newLowerValue);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setLowerValue(MinValueIndicator newLowerValue) {
        if (newLowerValue != lowerValue) {
            NotificationChain msgs = null;
            if (lowerValue != null)
                msgs = ((InternalEObject) lowerValue).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
                        - IndicatorsPackage.RANGE_INDICATOR__LOWER_VALUE, null, msgs);
            if (newLowerValue != null)
                msgs = ((InternalEObject) newLowerValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
                        - IndicatorsPackage.RANGE_INDICATOR__LOWER_VALUE, null, msgs);
            msgs = basicSetLowerValue(newLowerValue, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.RANGE_INDICATOR__LOWER_VALUE, newLowerValue,
                    newLowerValue));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MaxValueIndicator getUpperValue() {
        return upperValue;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetUpperValue(MaxValueIndicator newUpperValue, NotificationChain msgs) {
        MaxValueIndicator oldUpperValue = upperValue;
        upperValue = newUpperValue;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                    IndicatorsPackage.RANGE_INDICATOR__UPPER_VALUE, oldUpperValue, newUpperValue);
            if (msgs == null)
                msgs = notification;
            else
                msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setUpperValue(MaxValueIndicator newUpperValue) {
        if (newUpperValue != upperValue) {
            NotificationChain msgs = null;
            if (upperValue != null)
                msgs = ((InternalEObject) upperValue).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
                        - IndicatorsPackage.RANGE_INDICATOR__UPPER_VALUE, null, msgs);
            if (newUpperValue != null)
                msgs = ((InternalEObject) newUpperValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
                        - IndicatorsPackage.RANGE_INDICATOR__UPPER_VALUE, null, msgs);
            msgs = basicSetUpperValue(newUpperValue, msgs);
            if (msgs != null)
                msgs.dispatch();
        } else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.RANGE_INDICATOR__UPPER_VALUE, newUpperValue,
                    newUpperValue));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public int getDatatype() {
        return datatype;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setDatatype(int newDatatype) {
        int oldDatatype = datatype;
        datatype = newDatatype;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.RANGE_INDICATOR__DATATYPE, oldDatatype,
                    datatype));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public String getRange() {
        if (upperValue != null && lowerValue != null) {
            
            if (Java2SqlType.isNumbericInSQL(upperValue.getDatatype())) {
                Double upper = upperValue.getRealValue();
                Double lower = lowerValue.getRealValue();
                if (upper != null && lower != null) {
                    double range = upper - lower;
                    return String.valueOf(range);
                }
            } else if (Java2SqlType.isDateInSQL(upperValue.getDatatype())) {
                Date upper = null;
                Date lower = null;
                try {
                    // FIXME we need to know the format in order to parse the dates.
                    upper = DateUtils.parse(DateUtils.PATTERN_3, upperValue.getValue());
                    lower = DateUtils.parse(DateUtils.PATTERN_3, lowerValue.getValue());
                } catch (ParseException e) {
                    log.error("Parsing exception when trying to compute range of dates.", e);
                }
                if (upper != null && lower != null) {
                    return String.valueOf(ElapsedTime.getNbDays(upper, lower));
                } 
            }
            
   
        }

        return null;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
        case IndicatorsPackage.RANGE_INDICATOR__LOWER_VALUE:
            return basicSetLowerValue(null, msgs);
        case IndicatorsPackage.RANGE_INDICATOR__UPPER_VALUE:
            return basicSetUpperValue(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case IndicatorsPackage.RANGE_INDICATOR__LOWER_VALUE:
            return getLowerValue();
        case IndicatorsPackage.RANGE_INDICATOR__UPPER_VALUE:
            return getUpperValue();
        case IndicatorsPackage.RANGE_INDICATOR__DATATYPE:
            return new Integer(getDatatype());
        case IndicatorsPackage.RANGE_INDICATOR__RANGE:
            return getRange();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case IndicatorsPackage.RANGE_INDICATOR__LOWER_VALUE:
            setLowerValue((MinValueIndicator) newValue);
            return;
        case IndicatorsPackage.RANGE_INDICATOR__UPPER_VALUE:
            setUpperValue((MaxValueIndicator) newValue);
            return;
        case IndicatorsPackage.RANGE_INDICATOR__DATATYPE:
            setDatatype(((Integer) newValue).intValue());
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case IndicatorsPackage.RANGE_INDICATOR__LOWER_VALUE:
            setLowerValue((MinValueIndicator) null);
            return;
        case IndicatorsPackage.RANGE_INDICATOR__UPPER_VALUE:
            setUpperValue((MaxValueIndicator) null);
            return;
        case IndicatorsPackage.RANGE_INDICATOR__DATATYPE:
            setDatatype(DATATYPE_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case IndicatorsPackage.RANGE_INDICATOR__LOWER_VALUE:
            return lowerValue != null;
        case IndicatorsPackage.RANGE_INDICATOR__UPPER_VALUE:
            return upperValue != null;
        case IndicatorsPackage.RANGE_INDICATOR__DATATYPE:
            return datatype != DATATYPE_EDEFAULT;
        case IndicatorsPackage.RANGE_INDICATOR__RANGE:
            return RANGE_EDEFAULT == null ? getRange() != null : !RANGE_EDEFAULT.equals(getRange());
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (datatype: ");
        result.append(datatype);
        result.append(')');
        return result.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.CompositeIndicatorImpl#getChildIndicators()
     * 
     * ADDED scorreia 2008-05-22 getChildIndicators()
     */
    @Override
    public EList<Indicator> getChildIndicators() {
        EList<Indicator> children = new BasicEList<Indicator>();
        children.add(this.getLowerValue());
        children.add(this.getUpperValue());
        return children;
    }
} // RangeIndicatorImpl
