/*
 * JCaptcha, the open source java framework for captcha definition and integration
 * Copyright (c)  2007 jcaptcha.net. All Rights Reserved.
 * See the LICENSE.txt file distributed with this package.
 */

/*
 * jcaptcha, the open source java framework for captcha definition and integration
 * copyright (c)  2007 jcaptcha.net. All Rights Reserved.
 * See the LICENSE.txt file distributed with this package.
 */

/*
 * jcaptcha, the open source java framework for captcha definition and integration
 * copyright (c)  2007 jcaptcha.net. All Rights Reserved.
 * See the LICENSE.txt file distributed with this package.
 */

package br.usp.ime.cogroo.servlets;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.List;

import com.jhlabs.image.PinchFilter;
import com.jhlabs.math.ImageFunction2D;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.fontgenerator.TwistedAndShearedRandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsUsingShapeVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor;
import com.octo.captcha.component.image.textpaster.textdecorator.BaffleTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.DictionaryWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;

/**
 * <p/>
 * This is the default captcha engine. It provides a sample gimpy challenge that has no automated solution known. It is
 * based on the Baffle SPARC Captcha.
 * <p/>
 * </p>
 *
 * @author <a href="mailto:mag@jcaptcha.net">Marc-Antoine Garrigue</a>
 * @version 1.0
 */
public class ImageEngine extends ListImageCaptchaEngine {

    protected void buildInitialFactories() {

        //build filters
        com.jhlabs.image.EmbossFilter emboss = new com.jhlabs.image.EmbossFilter();
        com.jhlabs.image.SphereFilter sphere = new com.jhlabs.image.SphereFilter();
        com.jhlabs.image.RippleFilter rippleBack = new com.jhlabs.image.RippleFilter();
        com.jhlabs.image.RippleFilter ripple = new com.jhlabs.image.RippleFilter();
        com.jhlabs.image.TwirlFilter twirl = new com.jhlabs.image.TwirlFilter();
        com.jhlabs.image.WaterFilter water = new com.jhlabs.image.WaterFilter();

        com.jhlabs.image.WeaveFilter weaves = new com.jhlabs.image.WeaveFilter();


        //emboss.setBumpHeight(1.5d);

        ripple.setWaveType(com.jhlabs.image.RippleFilter.NOISE);
        ripple.setXAmplitude(3);
        ripple.setYAmplitude(3);
        ripple.setXWavelength(20);
        ripple.setYWavelength(10);
        ripple.setEdgeAction(com.jhlabs.image.TransformFilter.CLAMP);

        rippleBack.setWaveType(com.jhlabs.image.RippleFilter.NOISE);
        rippleBack.setXAmplitude(5);
        rippleBack.setYAmplitude(5);
        rippleBack.setXWavelength(10);
        rippleBack.setYWavelength(10);
        rippleBack.setEdgeAction(com.jhlabs.image.TransformFilter.CLAMP);

        water.setAmplitude(1);

        water.setWavelength(20);

        twirl.setAngle(3 / 360);

        sphere.setRefractionIndex(1);

        weaves.setUseImageColors(true);



        ImageDeformation rippleDef = new ImageDeformationByFilters(
                new ImageFilter[]{});
        ImageDeformation waterDef = new ImageDeformationByFilters(
                new ImageFilter[]{});
        ImageDeformation embossDef = new ImageDeformationByFilters(
                new ImageFilter[]{});
        ImageDeformation rippleDefBack = new ImageDeformationByFilters(
                new ImageFilter[]{});

        ImageDeformation weavesDef = new ImageDeformationByFilters(
                new ImageFilter[]{});

        ImageDeformation none = new ImageDeformationByFilters(null);

        //word generator
        com.octo.captcha.component.word.wordgenerator.WordGenerator words = //new ConstantWordGenerator("gefefi");
                new com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator(
                        new com.octo.captcha.component.word.FileDictionary(
                                "jcaptcha"));


        //wordtoimage components
              TextPaster paster = new DecoratedRandomTextPaster(new Integer(6), new Integer(
                      7), new SingleColorGenerator(Color.black)
                      , new TextDecorator[]{new BaffleTextDecorator(new Integer(1), Color.white)});
              BackgroundGenerator back = new UniColorBackgroundGenerator(
                new Integer(200), new Integer(100), Color.white);
        //BackgroundGenerator back = new FunkyBackgroundGenerator(new Integer(200), new Integer(100));
        FontGenerator font = new TwistedAndShearedRandomFontGenerator(
                new Integer(30), new Integer(40));
        //Add factories
        WordToImage word2image = new ComposedWordToImage(font, back, paster);
        this.addFactory(
                new com.octo.captcha.image.gimpy.GimpyFactory(words,
                        word2image));
        //build factories
        word2image = new DeformedComposedWordToImage(font, back, paster,
                rippleDef,
                waterDef,
                embossDef);
        this.addFactory(new GimpyFactory(words, word2image));
        //      select filters for 2
        word2image = new DeformedComposedWordToImage(font, back, paster,
                rippleDefBack,null,
                rippleDef);
        this.addFactory(new GimpyFactory(words, word2image));
        //select filters for 3
        word2image = new DeformedComposedWordToImage(font, back, paster,
                rippleDefBack,
                none,
                weavesDef);
        this.addFactory(new GimpyFactory(words, word2image));

    }


    /**
     * this method should be implemented as folow : <ul> <li>First construct all the factories you want to initialize
     * the gimpy with</li> <li>then call the this.addFactoriy method for each factory</li> </ul>
     */
    protected void buildInitialFactories2() {

        //word generator
        com.octo.captcha.component.word.wordgenerator.WordGenerator dictionnaryWords = //new ConstantWordGenerator("gefefi");
                new com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator(
                        new com.octo.captcha.component.word.FileDictionary(
                                "jcaptcha"));
        //wordtoimage components
        TextPaster randomPaster = new GlyphsPaster(7, 7,
                new RandomListColorGenerator(
                        new Color[]{
                                new Color(23, 170, 27),
                                new Color(220, 34, 11),
                                new Color(23, 67, 172)})
                ,new GlyphsVisitors[]{
                new TranslateGlyphsVerticalRandomVisitor(1),
               // new RotateGlyphsRandomVisitor(Math.PI/32),
                new OverlapGlyphsUsingShapeVisitor(3),
                new TranslateAllToRandomPointVisitor()
                //,

               //
                });
        /*
         new TextVisitor[]{
                new OverlapGlyphsTextVisitor(6)
        }, null
         */
        BackgroundGenerator back = new UniColorBackgroundGenerator(
                200, 70, Color.white);

        FontGenerator shearedFont = new RandomFontGenerator(50,
                50,
                new Font[]{
                        new Font("nyala",Font.BOLD, 50)
                        ,
                        new Font("Bell MT",  Font.PLAIN, 50)
                        ,
                        new Font("Credit valley",  Font.BOLD, 50)
                }
        ,false);


        PinchFilter pinch = new PinchFilter();

        pinch.setAmount(-.5f);
        pinch.setRadius(70);
        pinch.setAngle((float) (Math.PI/16));
        pinch.setCentreX(0.5f);
        pinch.setCentreY(-0.01f);
        pinch.setEdgeAction(ImageFunction2D.CLAMP);

        PinchFilter pinch2 = new PinchFilter();
        pinch2.setAmount(-.6f);
        pinch2.setRadius(70);
        pinch2.setAngle((float) (Math.PI/16));
        pinch2.setCentreX(0.3f);
        pinch2.setCentreY(1.01f);
        pinch2.setEdgeAction(ImageFunction2D.CLAMP);

        PinchFilter pinch3 = new PinchFilter();
        pinch3.setAmount(-.6f);
        pinch3.setRadius(70);
        pinch3.setAngle((float) (Math.PI/16));
        pinch3.setCentreX(0.8f);
        pinch3.setCentreY(-0.01f);
        pinch3.setEdgeAction(ImageFunction2D.CLAMP);



        List<ImageDeformation> textDef =  new ArrayList<ImageDeformation>();
        textDef.add(new ImageDeformationByBufferedImageOp(pinch));
        textDef.add(new ImageDeformationByBufferedImageOp(pinch2));
        textDef.add(new ImageDeformationByBufferedImageOp(pinch3));

        //word2image 1
        com.octo.captcha.component.image.wordtoimage.WordToImage word2image;
        word2image = new DeformedComposedWordToImage(false,shearedFont, back, randomPaster,
                new ArrayList<ImageDeformation>(),
                new ArrayList<ImageDeformation>(),
                textDef


        );


        this.addFactory(
                new com.octo.captcha.image.gimpy.GimpyFactory(dictionnaryWords,
                        word2image, false));

    }
}