package ru.modemastudio.notepro.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.modemastudio.notepro.R
import ru.modemastudio.notepro.databinding.FragmentOnboardingBinding
import ru.modemastudio.notepro.persistence.PreferencesManager
import ru.modemastudio.notepro.util.appComponent
import ru.modemastudio.notepro.util.clearDrawables
import ru.modemastudio.notepro.util.setDrawableEnd
import javax.inject.Inject

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val binding by viewBinding(FragmentOnboardingBinding::bind)

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.actionBar?.hide()
        context.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragments = arrayListOf(
            Fragment(R.layout.page_onboarding_first),
            Fragment(R.layout.page_onboarding_second),
            Fragment(R.layout.page_onboarding_third)
        )

        binding.onboardingPager.adapter =
            OnboardingPagerAdapter(childFragmentManager, lifecycle, fragments)

        binding.onboardingPager.registerOnPageChangeCallback(
            object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.onboardingBackBtn.isVisible = position > 0
                    // Change label on next button to finish button on last fragment
                    // and next for others
                    if (position == fragments.size - 1) {
                        binding.onboardingNextBtn.apply {
                            text = getString(R.string.finish)
                            clearDrawables()
                        }
                    } else {
                        binding.onboardingNextBtn.apply {
                            text = getString(R.string.next)
                            setDrawableEnd(R.drawable.ic_arrow_forward)
                        }
                    }
                }
            })

        binding.onboardingNextBtn.setOnClickListener {
            // Navigate to next fragment if its possible
            if (binding.onboardingPager.currentItem < fragments.size - 1)
                binding.onboardingPager.currentItem++
            // Finish onboarding when next button clicked on last fragment
            else if (binding.onboardingPager.currentItem == fragments.size - 1)
                finishOnboarding()
        }

        binding.onboardingBackBtn.setOnClickListener {
            if (binding.onboardingPager.currentItem > 0)
                binding.onboardingPager.currentItem--
        }

        binding.onboardingPageIndicator.attachToPager(binding.onboardingPager)
    }

    private fun finishOnboarding() {
        preferencesManager.isOnboardingDone = true
        findNavController().navigate(OnboardingFragmentDirections.actionOnboardingToNotesList())
    }
}