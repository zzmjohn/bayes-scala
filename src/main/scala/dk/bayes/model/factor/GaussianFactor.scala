package dk.bayes.model.factor

import scala.math.abs

import dk.bayes.gaussian.Gaussian

/**
 * This class represents a factor for a Univariate Gaussian Distribution.
 *
 * @author Daniel Korzekwa
 *
 * @param varId Factor variable id
 * @param m Mean
 * @param v Variance
 */
case class GaussianFactor(varId: Int, m: Double, v: Double) extends Factor {

  def getVariableIds(): Seq[Int] = Vector(varId)

  def marginal(marginalVarId: Int): GaussianFactor = {
    require(marginalVarId == varId, "Incorrect variable id")
    this.copy()
  }

  def productMarginal(marginalVarId: Int, factors: Seq[Factor]): GaussianFactor = {
    require(marginalVarId == varId, "Incorrect variable id")
    factors.foldLeft(this)((f1, f2) => f1 * f2)
  }

  def withEvidence(varId: Int, varValue: AnyVal): GaussianFactor = throw new UnsupportedOperationException("Not implemented yet")

  def getValue(assignment: (Int, AnyVal)*): Double = throw new UnsupportedOperationException("Not implemented yet")

  def *(factor: Factor): GaussianFactor = {

    factor match {
      case factor: GaussianFactor => {
        require(factor.varId == varId, "Can't multiply two gaussian factors: Factor variable ids do not match")
        val productGaussian = Gaussian(m, v) * Gaussian(factor.m, factor.v)
        val productFactor = GaussianFactor(varId, productGaussian.m, productGaussian.v)
        productFactor
      }
      case _ => throw new IllegalArgumentException("Gaussian factor cannot be multiplied by a factor that is non gaussian")
    }

  }

  def /(factor: Factor): GaussianFactor = {
    factor match {
      case factor: GaussianFactor => {
        require(factor.varId == varId, "Can't divide two gaussian factors: Factor variable ids do not match")
        val divideGaussian = Gaussian(m, v) / Gaussian(factor.m, factor.v)
        val divideFactor = GaussianFactor(varId, divideGaussian.m, divideGaussian.v)
        divideFactor
      }
      case _ => throw new IllegalArgumentException("Gaussian factor cannot be divided by a factor that is non gaussian")
    }
  }

  def equals(that: Factor, threshold: Double): Boolean = {
    val gaussianFactor = that.asInstanceOf[GaussianFactor]

    val thesame = (abs(m - gaussianFactor.m) < threshold && abs(v - gaussianFactor.v) < threshold) ||
      (m.isNaN() && gaussianFactor.m.isNaN() && v.isPosInfinity && gaussianFactor.v.isPosInfinity)
    thesame
  }

  override def toString() = "GaussianFactor(%d,%.3f,%.3f)".format(varId, m, v)
}