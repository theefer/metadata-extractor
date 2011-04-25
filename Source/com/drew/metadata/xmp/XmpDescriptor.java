/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * This is an addon to Drew Noakes metadata-extractor Library.
 * The addon is based on Drew's interface classes.
 *
 * Created by Torsten Skadell on 15-Jan-2008
 * Updated by Drew Noakes on 24-04-2011
 */
package com.drew.metadata.xmp;

import com.drew.imaging.PhotographicConversions;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;
import com.drew.metadata.exif.ExifDirectory;

import java.text.DecimalFormat;

/**
 * Contains all logic for the presentation of xmp data, as stored in Xmp-Segment.  Use
 * this class to provide human-readable descriptions of tag values.
 */
public class XmpDescriptor extends TagDescriptor
{
    // TODO some of these methods look similar to those found in ExifDescriptor... extract common functionality from both

    private static final java.text.DecimalFormat SimpleDecimalFormatter = new DecimalFormat("0.#");

    public XmpDescriptor(Directory directory)
    {
        super(directory);
    }

    /** Do some simple formatting, dependant upon tagType */
    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case XmpDirectory.TAG_MAKE:
            case XmpDirectory.TAG_MODEL:
                return _directory.getString(tagType);
            case XmpDirectory.TAG_EXPOSURE_TIME:
                return getExposureTimeDescription();
            case XmpDirectory.TAG_EXPOSURE_PROG:
                return getExposureProgramDescription();
            case XmpDirectory.TAG_SHUTTER_SPEED:
                return getShutterSpeedDescription();
            case XmpDirectory.TAG_F_NUMBER:
                return getFNumberDescription();
            case XmpDirectory.TAG_LENS:
            case XmpDirectory.TAG_LENS_INFO:
            case XmpDirectory.TAG_SERIAL:
            case XmpDirectory.TAG_FIRMWARE:
                return _directory.getString(tagType);
            case XmpDirectory.TAG_FOCAL_LENGTH:
                return getFocalLengthDescription();
            case XmpDirectory.TAG_APERTURE_VALUE:
                return getApertureValueDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    /** Do a simple formatting like ExifDescriptor.java */
    public String getExposureTimeDescription()
    {
        if (!_directory.containsTag(XmpDirectory.TAG_EXPOSURE_TIME)) return null;
        return _directory.getString(XmpDirectory.TAG_EXPOSURE_TIME) + " sec";
    }

    /** This code is from ExifDescriptor.java */
    public String getExposureProgramDescription() throws MetadataException
    {
        if (!_directory.containsTag(XmpDirectory.TAG_EXPOSURE_PROG)) return null;
        // '1' means manual control, '2' program normal, '3' aperture priority,
        // '4' shutter priority, '5' program creative (slow program),
        // '6' program action(high-speed program), '7' portrait mode, '8' landscape mode.
        switch (_directory.getInt(XmpDirectory.TAG_EXPOSURE_PROG)) {
            case 1:
                return "Manual control";
            case 2:
                return "Program normal";
            case 3:
                return "Aperture priority";
            case 4:
                return "Shutter priority";
            case 5:
                return "Program creative (slow program)";
            case 6:
                return "Program action (high-speed program)";
            case 7:
                return "Portrait mode";
            case 8:
                return "Landscape mode";
            default:
                return "Unknown program (" + _directory.getInt(ExifDirectory.TAG_EXPOSURE_PROGRAM) + ")";
        }
    }


    /** This code is from ExifDescriptor.java */
    public String getShutterSpeedDescription() throws MetadataException
    {
        if (!_directory.containsTag(XmpDirectory.TAG_SHUTTER_SPEED)) return null;
        float apexValue = _directory.getFloat(XmpDirectory.TAG_SHUTTER_SPEED);

        // thanks to Mark Edwards for spotting and patching a bug in the calculation of this
        // description (spotted bug using a Canon EOS 300D)
        // thanks also to Gli Blr for spotting this bug
        if (apexValue <= 1) {
            float apexPower = (float) (1 / (Math.exp(apexValue * Math.log(2))));
            long apexPower10 = Math.round((double) apexPower * 10.0);
            float fApexPower = (float) apexPower10 / 10.0f;
            return fApexPower + " sec";
        } else {
            int apexPower = (int) ((Math.exp(apexValue * Math.log(2))));
            return "1/" + apexPower + " sec";
        }
    }

    /** Do a simple formatting like ExifDescriptor.java */
    public String getFNumberDescription() throws MetadataException
    {
        if (!_directory.containsTag(XmpDirectory.TAG_F_NUMBER)) return null;
        Rational fNumber = _directory.getRational(XmpDirectory.TAG_F_NUMBER);
        return "F" + SimpleDecimalFormatter.format(fNumber.doubleValue());
    }

    /** This code is from ExifDescriptor.java */
    public String getFocalLengthDescription() throws MetadataException
    {
        if (!_directory.containsTag(XmpDirectory.TAG_FOCAL_LENGTH)) return null;
        java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        Rational focalLength = _directory.getRational(XmpDirectory.TAG_FOCAL_LENGTH);
        return formatter.format(focalLength.doubleValue()) + " mm";
    }

    /** This code is from ExifDescriptor.java */
    public String getApertureValueDescription() throws MetadataException
    {
        if (!_directory.containsTag(XmpDirectory.TAG_APERTURE_VALUE)) return null;
        double aperture = _directory.getDouble(XmpDirectory.TAG_APERTURE_VALUE);
        double fStop = PhotographicConversions.apertureToFStop(aperture);
        return "F" + SimpleDecimalFormatter.format(fStop);
    }
}