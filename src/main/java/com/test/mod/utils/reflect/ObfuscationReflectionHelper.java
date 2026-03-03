package com.test.mod.utils.reflect;

import com.test.mod.asm.Type;
import com.test.mod.transformer.mapping.Mapping;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringJoiner;

public class ObfuscationReflectionHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker REFLECTION = MarkerManager.getMarker("REFLECTION");

    public ObfuscationReflectionHelper() {
    }

    public static String remapName(String desc, String name, Class<?> clazz) {
        return Mapping.get(clazz, name, desc);
    }

    public static <T, E> @Nullable T getPrivateValue(Class<? super E> classToAccess, E instance, String fieldName) {
        try {
            return (T) findField(classToAccess, fieldName).get(instance);
        } catch (UnableToFindFieldException var4) {
            LOGGER.error(REFLECTION, "Unable to locate field {} ({}) on type {}", fieldName, remapName(null, fieldName, classToAccess), classToAccess.getName(), var4);
            throw var4;
        } catch (IllegalAccessException var5) {
            LOGGER.error(REFLECTION, "Unable to access field {} ({}) on type {}", fieldName, remapName(null, fieldName, classToAccess), classToAccess.getName(), var5);
            throw new UnableToAccessFieldException(var5);
        }
    }

    public static <T, E> void setPrivateValue(@NotNull Class<? super T> classToAccess, @NotNull T instance, @Nullable E value, @NotNull String fieldName) {
        try {
            findField(classToAccess, fieldName).set(instance, value);
        } catch (UnableToFindFieldException var5) {
            LOGGER.error("Unable to locate any field {} on type {}", fieldName, classToAccess.getName(), var5);
            throw var5;
        } catch (IllegalAccessException var6) {
            LOGGER.error("Unable to set any field {} on type {}", fieldName, classToAccess.getName(), var6);
            throw new UnableToAccessFieldException(var6);
        }
    }
    private static void appendDescriptor(final Class<?> clazz, final StringBuilder stringBuilder) {
        Class<?> currentClass = clazz;
        while (currentClass.isArray()) {
            stringBuilder.append('[');
            currentClass = currentClass.getComponentType();
        }
        if (currentClass.isPrimitive()) {
            char descriptor;
            if (currentClass == Integer.TYPE) {
                descriptor = 'I';
            } else if (currentClass == Void.TYPE) {
                descriptor = 'V';
            } else if (currentClass == Boolean.TYPE) {
                descriptor = 'Z';
            } else if (currentClass == Byte.TYPE) {
                descriptor = 'B';
            } else if (currentClass == Character.TYPE) {
                descriptor = 'C';
            } else if (currentClass == Short.TYPE) {
                descriptor = 'S';
            } else if (currentClass == Double.TYPE) {
                descriptor = 'D';
            } else if (currentClass == Float.TYPE) {
                descriptor = 'F';
            } else if (currentClass == Long.TYPE) {
                descriptor = 'J';
            } else {
                throw new AssertionError();
            }
            stringBuilder.append(descriptor);
        } else {
            stringBuilder.append('L').append(Type.getInternalName(currentClass)).append(';');
        }
    }
    public static String getMethodDescriptor(Class<?> returnType, Class<?>... parameterTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');

        for (Class<?> parameter : parameterTypes) {
            appendDescriptor(parameter, stringBuilder);
        }
        stringBuilder.append(')');
        appendDescriptor(returnType, stringBuilder);
        return stringBuilder.toString();
    }
    public static @NotNull Method findMethod(@NotNull Class<?> clazz, @NotNull String methodName,Class<?> returnType, Class<?>... parameterTypes) {
        Preconditions.checkNotNull(clazz, "Class to find method on cannot be null.");
        Preconditions.checkNotNull(methodName, "Name of method to find cannot be null.");
        Preconditions.checkArgument(!methodName.isEmpty(), "Name of method to find cannot be empty.");
        Preconditions.checkNotNull(parameterTypes, "Parameter types of method to find cannot be null.");

        try {
            Method m = clazz.getDeclaredMethod(remapName(getMethodDescriptor(returnType, parameterTypes), methodName, clazz), parameterTypes);
            m.setAccessible(true);
            return m;
        } catch (Exception var4) {
            throw new UnableToFindMethodException(var4);
        }
    }

    public static <T> @NotNull Constructor<T> findConstructor(@NotNull Class<T> clazz, Class<?>... parameterTypes) {
        Preconditions.checkNotNull(clazz, "Class to find constructor on cannot be null.");
        Preconditions.checkNotNull(parameterTypes, "Parameter types of constructor to find cannot be null.");

        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException var9) {
            StringBuilder desc = new StringBuilder();
            desc.append(clazz.getSimpleName());
            StringJoiner joiner = new StringJoiner(", ", "(", ")");
            Class[] var5 = parameterTypes;
            int var6 = parameterTypes.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Class<?> type = var5[var7];
                joiner.add(type.getSimpleName());
            }

            desc.append(joiner);
            String var10002 = desc.toString();
            throw new UnknownConstructorException("Could not find constructor '" + var10002 + "' in " + clazz);
        }
    }

    public static <T> @NotNull Field findField(@NotNull Class<? super T> clazz, @NotNull String fieldName) {
        Preconditions.checkNotNull(clazz, "Class to find field on cannot be null.");
        Preconditions.checkNotNull(fieldName, "Name of field to find cannot be null.");
        Preconditions.checkArgument(!fieldName.isEmpty(), "Name of field to find cannot be empty.");

        try {
            Field f = clazz.getDeclaredField(remapName(null, fieldName, clazz));
            f.setAccessible(true);
            return f;
        } catch (Exception var3) {
            throw new UnableToFindFieldException(var3);
        }
    }

    public static class UnableToFindFieldException extends RuntimeException {
        private UnableToFindFieldException(Exception e) {
            super(e);
        }
    }

    public static class UnableToAccessFieldException extends RuntimeException {
        private UnableToAccessFieldException(Exception e) {
            super(e);
        }
    }

    public static class UnableToFindMethodException extends RuntimeException {
        public UnableToFindMethodException(Throwable failed) {
            super(failed);
        }
    }

    public static class UnknownConstructorException extends RuntimeException {
        public UnknownConstructorException(String message) {
            super(message);
        }
    }
}
