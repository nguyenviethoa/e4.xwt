/*******************************************************************************
 * Copyright (c) 2006, 2008 Soyatec (http://www.soyatec.com) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soyatec - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.xwt.databinding;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateSetStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.IBindingContext;
import org.eclipse.e4.xwt.IDataBindingInfo;
import org.eclipse.e4.xwt.IValueConverter;
import org.eclipse.e4.xwt.IValidationRule;
import org.eclipse.e4.xwt.InverseValueConverter;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.internal.utils.ObjectUtil;
import org.eclipse.e4.xwt.validation.InverseValidationRule;

/**
 * @author jliu jin.liu@soyatec.com
 */
public class BindingContext implements IBindingContext {
	public static final UpdateSetStrategy POLICY_UPDATE = new UpdateSetStrategy(
			UpdateSetStrategy.POLICY_UPDATE);
	private final DataBindingContext dataBindingContext;

	public BindingContext(DataBindingContext dataBindingContext) {
		super();

		this.dataBindingContext = dataBindingContext;
	}

	public Binding bind(IObservableValue source, IObservableValue target) {
		return bind(source, target, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.databinding.IBindingContext#bind(org.eclipse.core.
	 * databinding.observable.value.IObservableValue,
	 * org.eclipse.core.databinding.observable.value.IObservableValue)
	 */
	public Binding bind(IObservable source, IObservable target,
			IDataBindingInfo dataBinding) {
		if (source instanceof IObservableValue
				&& target instanceof IObservableValue) {
			return bindValue((IObservableValue) source,
					(IObservableValue) target, dataBinding);
		} else if (source instanceof IObservableSet
				&& target instanceof IObservableSet) {
			IValueConverter converter = null;
			int sourceToTargetPolicy = UpdateSetStrategy.POLICY_UPDATE;
			int targetToSourcePolicy = UpdateSetStrategy.POLICY_UPDATE;
			// Set policy to UpdateValueStrategy.
			if (dataBinding != null) {
				switch (dataBinding.getMode()) {
				case OneWay:
					targetToSourcePolicy = UpdateSetStrategy.POLICY_NEVER;
					break;
				case OneTime:
					sourceToTargetPolicy = UpdateSetStrategy.POLICY_NEVER;
					targetToSourcePolicy = UpdateSetStrategy.POLICY_NEVER;
					break;
				default:
					break;
				}
				converter = dataBinding.getConverter();
			}
			UpdateSetStrategy sourceToTarget = new UpdateSetStrategy(
					sourceToTargetPolicy);
			UpdateSetStrategy targetToSource = new UpdateSetStrategy(
					targetToSourcePolicy);
			return bindSet((IObservableSet) target, (IObservableSet) source,
					targetToSource, sourceToTarget, converter);
		} else if (source instanceof IObservableList
				&& target instanceof IObservableList) {
			IValueConverter converter = null;
			int sourceToTargetPolicy = UpdateListStrategy.POLICY_UPDATE;
			int targetToSourcePolicy = UpdateListStrategy.POLICY_UPDATE;
			// Set policy to UpdateValueStrategy.
			if (dataBinding != null) {
				switch (dataBinding.getMode()) {
				case OneWay:
					targetToSourcePolicy = UpdateListStrategy.POLICY_NEVER;
					break;
				case OneTime:
					sourceToTargetPolicy = UpdateListStrategy.POLICY_NEVER;
					targetToSourcePolicy = UpdateListStrategy.POLICY_NEVER;
					break;
				default:
					break;
				}
				converter = dataBinding.getConverter();
			}
			UpdateListStrategy sourceToTarget = new UpdateListStrategy(
					sourceToTargetPolicy);
			UpdateListStrategy targetToSource = new UpdateListStrategy(
					targetToSourcePolicy);
			return bindList((IObservableList) target, (IObservableList) source,
					targetToSource, sourceToTarget, converter);
		}
		throw new IllegalStateException();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.databinding.IBindingContext#bind(org.eclipse.core.
	 * databinding.observable.value.IObservableValue,
	 * org.eclipse.core.databinding.observable.value.IObservableValue)
	 */
	public Binding bindList(IObservableList source, IObservableList target,
			UpdateListStrategy sourceToTarget,
			UpdateListStrategy targetToSource, IValueConverter converter) {
		if (converter != null) {
			return bindList(source, target, sourceToTarget, targetToSource,
					converter, new InverseValueConverter(converter));
		} else {
			return bindList(source, target, sourceToTarget, targetToSource,
					null, null);
		}
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param sourceToTarget
	 *            if it is null, the default converter will be update policy
	 * @param targetToSource
	 *            if it is null, the default converter will be update policy
	 * @param sourceToTargetConvertor
	 *            if it is null, the default converter will be used
	 * @param targetToSourceConvertor
	 *            if it is null, the default converter will be used
	 */
	public Binding bindList(IObservableList source, IObservableList target,
			UpdateListStrategy sourceToTarget,
			UpdateListStrategy targetToSource,
			IConverter sourceToTargetConvertor,
			IConverter targetToSourceConvertor) {
		if (source != null && target != null) {
			if (sourceToTarget == null) {
				sourceToTarget = new UpdateListStrategy(
						UpdateListStrategy.POLICY_UPDATE);
			}
			if (targetToSource == null) {
				targetToSource = new UpdateListStrategy(
						UpdateListStrategy.POLICY_UPDATE);
			}

			if (sourceToTargetConvertor != null) {
				sourceToTarget.setConverter(sourceToTargetConvertor);
			}

			if (targetToSourceConvertor != null) {
				targetToSource.setConverter(targetToSourceConvertor);
			}

			this.dataBindingContext.bindList(target, source, targetToSource,
					sourceToTarget);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.databinding.IBindingContext#bind(org.eclipse.core.
	 * databinding.observable.value.IObservableValue,
	 * org.eclipse.core.databinding.observable.value.IObservableValue)
	 */
	public Binding bindSet(IObservableSet source, IObservableSet target,
			UpdateSetStrategy sourceToTarget, UpdateSetStrategy targetToSource,
			IValueConverter converter) {
		if (converter != null) {
			return bindSet(source, target, sourceToTarget, targetToSource,
					converter, new InverseValueConverter(converter));
		} else {
			return bindSet(source, target, sourceToTarget, targetToSource,
					null, null);
		}
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param sourceToTarget
	 *            if it is null, the default converter will be update policy
	 * @param targetToSource
	 *            if it is null, the default converter will be update policy
	 * @param sourceToTargetConvertor
	 *            if it is null, the default converter will be used
	 * @param targetToSourceConvertor
	 *            if it is null, the default converter will be used
	 */
	public Binding bindSet(IObservableSet source, IObservableSet target,
			UpdateSetStrategy sourceToTarget, UpdateSetStrategy targetToSource,
			IConverter sourceToTargetConvertor,
			IConverter targetToSourceConvertor) {
		if (source != null && target != null) {
			if (sourceToTarget == null) {
				sourceToTarget = new UpdateSetStrategy(
						UpdateSetStrategy.POLICY_UPDATE);
			}
			if (targetToSource == null) {
				targetToSource = new UpdateSetStrategy(
						UpdateSetStrategy.POLICY_UPDATE);
			}

			if (sourceToTargetConvertor != null) {
				sourceToTarget.setConverter(sourceToTargetConvertor);
			}

			if (targetToSourceConvertor != null) {
				targetToSource.setConverter(targetToSourceConvertor);
			}

			this.dataBindingContext.bindSet(target, source, targetToSource,
					sourceToTarget);
		}
		return null;
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param sourceToTarget
	 *            if it is null, the default converter will be update policy
	 * @param targetToSource
	 *            if it is null, the default converter will be update policy
	 * @param sourceToTargetConvertor
	 *            if it is null, the default converter will be used
	 * @param targetToSourceConvertor
	 *            if it is null, the default converter will be used
	 */
	public Binding bind(IObservableList source, IObservableList target,
			UpdateListStrategy sourceToTarget,
			UpdateListStrategy targetToSource,
			IConverter sourceToTargetConvertor,
			IConverter targetToSourceConvertor) {
		if (source != null && target != null) {
			if (sourceToTarget == null) {
				sourceToTarget = new UpdateListStrategy(
						UpdateListStrategy.POLICY_UPDATE);
			}
			if (targetToSource == null) {
				targetToSource = new UpdateListStrategy(
						UpdateListStrategy.POLICY_UPDATE);
			}

			if (sourceToTargetConvertor != null) {
				sourceToTarget.setConverter(sourceToTargetConvertor);
			}

			if (targetToSourceConvertor != null) {
				targetToSource.setConverter(targetToSourceConvertor);
			}

			this.dataBindingContext.bindList(target, source, targetToSource,
					sourceToTarget);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.databinding.IBindingContext#bind(org.eclipse.core.
	 * databinding.observable.value.IObservableValue,
	 * org.eclipse.core.databinding.observable.value.IObservableValue)
	 */
	private Binding bindValue(IObservableValue source, IObservableValue target,
			IDataBindingInfo dataBinding) {
		IValueConverter converter = null;
		IValidationRule[] validators = null;
		int sourceToTargetPolicy = UpdateValueStrategy.POLICY_UPDATE;
		int targetToSourcePolicy = UpdateValueStrategy.POLICY_UPDATE;
		// Set policy to UpdateValueStrategy.
		if (dataBinding != null) {
			switch (dataBinding.getMode()) {
			case OneWay:
				targetToSourcePolicy = UpdateValueStrategy.POLICY_NEVER;
				break;
			case OneTime:
				sourceToTargetPolicy = UpdateValueStrategy.POLICY_NEVER;
				targetToSourcePolicy = UpdateValueStrategy.POLICY_NEVER;
				break;
			default:
				break;
			}
			converter = dataBinding.getConverter();
			validators = dataBinding.getValidators();
		}
		UpdateValueStrategy sourceToTarget = new UpdateValueStrategy(
				sourceToTargetPolicy);
		UpdateValueStrategy targetToSource = new UpdateValueStrategy(
				targetToSourcePolicy);

		bindValidators(validators, sourceToTarget, targetToSource);

		return bind(source, target, sourceToTarget, targetToSource, converter);
	}

	private void bindValidators(IValidationRule[] validators,
			UpdateValueStrategy sourceToTarget,
			UpdateValueStrategy targetToSource) {
		if (validators != null) {
			for (IValidationRule validator : validators) {
				switch (validator.getBindingMode()) {
				case SourceToTarget:
					addValidatorToStrategy(sourceToTarget, validator);
					break;
				case TargetToSource:
					addValidatorToStrategy(targetToSource,
							new InverseValidationRule(validator));
					break;
				case Both:
				default:
					addValidatorToStrategy(sourceToTarget, validator);
					addValidatorToStrategy(targetToSource,
							new InverseValidationRule(validator));
				}
			}
		}
	}

	private void addValidatorToStrategy(UpdateValueStrategy strategy,
			IValidationRule validator) {
		switch (validator.getPhase()) {
		case AfterGet:
			strategy.setAfterGetValidator(validator);
			break;
		case AfterConvert:
			strategy.setAfterConvertValidator(validator);
			break;
		case BeforeSet:
		default:
			strategy.setBeforeSetValidator(validator);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.e4.xwt.databinding.IBindingContext#bind(org.eclipse.core.
	 * databinding.observable.value.IObservableValue,
	 * org.eclipse.core.databinding.observable.value.IObservableValue)
	 */
	public Binding bind(IObservableValue source, IObservableValue target,
			UpdateValueStrategy sourceToTarget,
			UpdateValueStrategy targetToSource, IValueConverter converter) {
		if (converter != null) {
			return bind(source, target, sourceToTarget, targetToSource,
					converter, new InverseValueConverter(converter));
		} else {
			return bind(source, target, sourceToTarget, targetToSource, null,
					null);
		}
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param sourceToTarget
	 *            if it is null, the default converter will be update policy
	 * @param targetToSource
	 *            if it is null, the default converter will be update policy
	 * @param sourceToTargetConvertor
	 *            if it is null, the default converter will be used
	 * @param targetToSourceConvertor
	 *            if it is null, the default converter will be used
	 */
	public Binding bind(IObservableValue source, IObservableValue target,
			UpdateValueStrategy sourceToTarget,
			UpdateValueStrategy targetToSource,
			IConverter sourceToTargetConvertor,
			IConverter targetToSourceConvertor) {
		if (source != null && target != null) {
			if (sourceToTarget == null) {
				sourceToTarget = new UpdateValueStrategy(
						UpdateValueStrategy.POLICY_UPDATE);
			}
			if (targetToSource == null) {
				targetToSource = new UpdateValueStrategy(
						UpdateValueStrategy.POLICY_UPDATE);
			}

			// Add converter to UpdateValueStrategy.
			Object sourceValueType = source.getValueType();
			if (sourceValueType == null) {
				sourceValueType = Object.class;
			}
			Object targetValueType = target.getValueType();
			if (targetValueType == null) {
				targetValueType = Object.class;
			}
			Class<?> sourceType = (sourceValueType instanceof Class<?>) ? (Class<?>) sourceValueType
					: sourceValueType.getClass();
			Class<?> targetType = (targetValueType instanceof Class<?>) ? (Class<?>) targetValueType
					: targetValueType.getClass();
			if (sourceType == null) {
				sourceType = Object.class;
			} else {
				sourceType = ObjectUtil.normalizedType(sourceType);
			}

			if (targetType == null) {
				targetType = Object.class;
			} else {
				targetType = ObjectUtil.normalizedType(targetType);
			}

			if (sourceToTargetConvertor != null) {
				sourceToTarget.setConverter(sourceToTargetConvertor);
			} else if (!targetType.isAssignableFrom(sourceType)) {
				IConverter m2t = XWT.findConvertor(sourceType, targetType);
				if (m2t != null) {
					sourceToTarget.setConverter(m2t);
				}
			}

			if (targetToSourceConvertor != null) {
				targetToSource.setConverter(targetToSourceConvertor);
			} else if (!sourceType.isAssignableFrom(targetType)) {
				IConverter t2m = XWT.findConvertor(targetType, sourceType);
				if (t2m != null) {
					targetToSource.setConverter(t2m);
				}
			}

			this.dataBindingContext.bindValue(target, source, targetToSource,
					sourceToTarget);
		}
		return null;
	}
}
