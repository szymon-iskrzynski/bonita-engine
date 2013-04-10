/**
 * Copyright (C) 2011-2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package com.bonitasoft.engine.api;

import java.util.Map;

import org.bonitasoft.engine.exception.InvalidSessionException;
import org.bonitasoft.engine.exception.MonitoringException;
import org.bonitasoft.engine.exception.UnavailableInformationException;
import org.bonitasoft.engine.management.GcInfo;

/**
 * @author Zhao Na
 * @author Elias Ricken de Medeiros
 * @author Feng Hui
 * @author Matthieu Chaffotte
 */
public interface PlatformMonitoringAPI {

    /**
     * Get the sum of both heap and non-heap memory usage.
     * 
     * @return a quantity number of memory occupied currently
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    long getCurrentMemoryUsage() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the percentage of memory used compare to maximum available memory.
     * This calculation is based on both the heap & non-heap maximum amount of memory that can be used.
     * 
     * @return a percentage of memory occupied compare to maximum available memory
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    float getMemoryUsagePercentage() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the system load average for the last minute.
     * The system load average is the sum of the number of runnable entities queued to the available
     * processors and the number of runnable entities running on the available processors averaged over
     * a period of time. The way in which the load average is calculated is operating system specific
     * but is typically a damped time-dependent average.
     * If the load average is not available, a negative value is returned.
     * 
     * @return a average number of system load for the last minute
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    double getSystemLoadAverage() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the number of milliseconds elapsed since the Java Virtual Machine started.
     * 
     * @return a number of milliseconds elapsed since the Java Virtual Machine started
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    long getUpTime() throws MonitoringException, InvalidSessionException;

    /**
     * Returns a timestamp (in millisecond) which indicates the date when the Java virtual
     * machine started.
     * Usually, a timestamp represents the time elapsed since the 1st of January, 1970.
     * 
     * @return a timestamp (in millisecond) which indicates the date when the Java virtual machine started
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    long getStartTime() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the total CPU time for all live threads in nanoseconds. It sums the CPU time
     * consumed by each live threads.
     * 
     * @return the total CPU time for all live threads in nanoseconds
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    long getTotalThreadsCpuTime() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the current number of live threads including both daemon and non-daemon threads.
     * 
     * @return the current number of live threads including both daemon and non-daemon threads
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    int getThreadCount() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the number of processors available to the Java virtual machine.
     * 
     * @return the number of processors available to the Java virtual machine
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    int getAvailableProcessors() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the operating system architecture.
     * 
     * @return the operating system architecture as a string
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    String getOSArch() throws MonitoringException, InvalidSessionException;

    /**
     * Return the OS name.
     * 
     * @return the OS name
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    String getOSName() throws MonitoringException, InvalidSessionException;

    /**
     * Return the OS version.
     * 
     * @return the OS version
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    String getOSVersion() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the Java virtual machine implementation name.
     * 
     * @return the Java virtual machine implementation name
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    String getJvmName() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the Java virtual machine implementation vendor.
     * 
     * @return the Java virtual machine implementation vendor
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    String getJvmVendor() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the Java virtual machine implementation version.
     * 
     * @return the Java virtual machine implementation version
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    String getJvmVersion() throws MonitoringException, InvalidSessionException;

    /**
     * Returns the Java virtual machine System properties list.
     * 
     * @return a map of the Java virtual machine System properties
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @since6.0
     */
    Map<String, String> getJvmSystemProperties() throws MonitoringException, InvalidSessionException;

    /**
     * Check if the scheduler is started.
     * If it is started, return true. else return false.
     * 
     * @return if the scheduler is started
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @since6.0
     */
    boolean isSchedulerStarted() throws InvalidSessionException, MonitoringException;

    /**
     * Get the number of all active transactions
     * If no active transactions there, return 0
     * 
     * @return a number of active transaction
     * @throws InvalidSessionException
     *             Generic exception thrown if API Session is invalid, e.g session has expired.
     * @throws MonitoringException
     *             occurs when an exception is thrown during monitoring
     * @since6.0
     */
    long getNumberOfActiveTransactions() throws InvalidSessionException, MonitoringException;

    /**
     * Returns the CPU time used by the process on which the Java virtual machine is running in nanoseconds. The returned value is of nanoseconds precision but
     * not necessarily nanoseconds accuracy. This method returns -1 if the the platform does not support this operation.
     * 
     * @return the CPU time used by the process in nanoseconds, or -1 if this operation is not supported.
     * @throws InvalidSessionException
     * @throws MonitoringException
     * @since6.0
     */
    long getProcessCpuTime() throws InvalidSessionException, MonitoringException, UnavailableInformationException;

    /**
     * Returns the amount of virtual memory that is guaranteed to be available to the running process in bytes, or -1 if this operation is not supported.
     * @since6.0
     */
    long getCommittedVirtualMemorySize() throws InvalidSessionException, MonitoringException, UnavailableInformationException;

    /**
     * Returns the total amount of swap space in bytes.
     * @since6.0
     */
    long getTotalSwapSpaceSize() throws InvalidSessionException, MonitoringException, UnavailableInformationException;

    /**
     * Returns the amount of free swap space in bytes.
     * @since6.0
     */
    long getFreeSwapSpaceSize() throws InvalidSessionException, MonitoringException, UnavailableInformationException;

    /**
     * Returns the amount of free physical memory in bytes.
     * @since6.0
     */
    long getFreePhysicalMemorySize() throws InvalidSessionException, MonitoringException, UnavailableInformationException;

    /**
     * Returns the total amount of physical memory in bytes.
     * @since6.0
     */
    long getTotalPhysicalMemorySize() throws InvalidSessionException, MonitoringException, UnavailableInformationException;

    /**
     * Returns true if engine is running on top of a SUN/Oracle JVM
     * @since6.0
     */
    boolean isOptionalMonitoringInformationAvailable() throws InvalidSessionException, MonitoringException;

    /**
     * Returns the last GC info.
     * The key 'GcName' of the outer map is the GarbageCollectorMXBean's instance name.
     * The inner Map<String key, String value> represents the LastGcInfo instance.
     * The possible keys are StartTime, EndTime, Duration, MemoryUsageBeforeGc and MemoryUsageAfterGc.
     * 
     * @return
     * @throws InvalidSessionException
     * @throws MonitoringException
     * @throws UnavailableInformationException
     * @since6.0
     */
    Map<String, GcInfo> getLastGcInfo() throws InvalidSessionException, MonitoringException, UnavailableInformationException;

}