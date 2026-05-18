<script setup lang="ts">

import EyeBall from "/@/components/animated-characters/EyeBall.vue";
import Pupil from "/@/components/animated-characters/Pupil.vue";
import gsap from 'gsap'

const props = defineProps({
  isTyping: {type: Boolean, default: false},
  showPassword: {type: Boolean, default: false},
  passwordLength: {type: Number, default: 0}
})
const containerRef = ref<HTMLDivElement>()
const mouseRef = ref({x: 0, y: 0})
const rafIdRef = ref(0)

const purpleRef = ref<HTMLDivElement | null>(null)
const blackRef = ref<HTMLDivElement | null>(null)
const orangeRef = ref<HTMLDivElement>()
const yellowRef = ref<HTMLDivElement>()

const purpleFaceRef = ref<HTMLDivElement>()
const blackFaceRef = ref<HTMLDivElement>()
const orangeFaceRef = ref<HTMLDivElement>()
const yellowFaceRef = ref<HTMLDivElement>()

const yellowMouthRef = ref<HTMLDivElement>()

const purpleBlinkTimerRef = ref<number | undefined>(undefined)
const blackBlinkTimerRef = ref<number | undefined>(undefined)
const purplePeekTimerRef = ref<ReturnType<typeof setTimeout>>()

const isLookingRef = ref(false);
const lookingTimerRef = ref<number | undefined>(undefined)

const isHidingPassword = computed(() => props.passwordLength > 0 && !props.showPassword);
const isShowingPassword = computed(() => props.passwordLength > 0 && props.showPassword);

const state = reactive({
  isTyping: props.isTyping,
  isHidingPassword: isHidingPassword.value,
  isShowingPassword: isShowingPassword.value,
  isLooking: isLookingRef.value
})

let quickToInstances: {
  purpleSkew: gsap.QuickToFunc;
  blackSkew: gsap.QuickToFunc;
  orangeSkew: gsap.QuickToFunc;
  yellowSkew: gsap.QuickToFunc;
  purpleX: gsap.QuickToFunc;
  blackX: gsap.QuickToFunc;
  purpleHeight: gsap.QuickToFunc;
  purpleFaceLeft: gsap.QuickToFunc;
  purpleFaceTop: gsap.QuickToFunc;
  blackFaceLeft: gsap.QuickToFunc;
  blackFaceTop: gsap.QuickToFunc;
  orangeFaceX: gsap.QuickToFunc;
  orangeFaceY: gsap.QuickToFunc;
  yellowFaceX: gsap.QuickToFunc;
  yellowFaceY: gsap.QuickToFunc;
  mouthX: gsap.QuickToFunc;
  mouthY: gsap.QuickToFunc;
} | null = null
const onMove = (e: MouseEvent) => {
  mouseRef.value = {x: e.clientX, y: e.clientY}
}
const initQuickTo = () => {
  nextTick(() => {
    if (!purpleRef.value || !blackRef.value || !orangeRef.value || !yellowRef.value ||
        !purpleFaceRef.value || !blackFaceRef.value || !orangeFaceRef.value || !yellowFaceRef.value ||
        !yellowMouthRef.value) {
      return
    }
    quickToInstances = {
      purpleSkew: gsap.quickTo(purpleRef.value, 'skewX', {duration: 0.3, ease: 'power2.out'}),
      blackSkew: gsap.quickTo(blackRef.value, 'skewX', {duration: 0.3, ease: 'power2.out'}),
      orangeSkew: gsap.quickTo(orangeRef.value, 'skewX', {duration: 0.3, ease: 'power2.out'}),
      yellowSkew: gsap.quickTo(yellowRef.value, 'skewX', {duration: 0.3, ease: 'power2.out'}),
      purpleX: gsap.quickTo(purpleRef.value, 'x', {duration: 0.3, ease: 'power2.out'}),
      blackX: gsap.quickTo(blackRef.value, 'x', {duration: 0.3, ease: 'power2.out'}),
      purpleHeight: gsap.quickTo(purpleRef.value, 'height', {duration: 0.3, ease: 'power2.out'}),
      purpleFaceLeft: gsap.quickTo(purpleFaceRef.value, 'left', {duration: 0.3, ease: 'power2.out'}),
      purpleFaceTop: gsap.quickTo(purpleFaceRef.value, 'top', {duration: 0.3, ease: 'power2.out'}),
      blackFaceLeft: gsap.quickTo(blackFaceRef.value, 'left', {duration: 0.3, ease: 'power2.out'}),
      blackFaceTop: gsap.quickTo(blackFaceRef.value, 'top', {duration: 0.3, ease: 'power2.out'}),
      orangeFaceX: gsap.quickTo(orangeFaceRef.value, 'x', {duration: 0.2, ease: 'power2.out'}),
      orangeFaceY: gsap.quickTo(orangeFaceRef.value, 'y', {duration: 0.2, ease: 'power2.out'}),
      yellowFaceX: gsap.quickTo(yellowFaceRef.value, 'x', {duration: 0.2, ease: 'power2.out'}),
      yellowFaceY: gsap.quickTo(yellowFaceRef.value, 'y', {duration: 0.2, ease: 'power2.out'}),
      mouthX: gsap.quickTo(yellowMouthRef.value, 'x', {duration: 0.2, ease: 'power2.out'}),
      mouthY: gsap.quickTo(yellowMouthRef.value, 'y', {duration: 0.2, ease: 'power2.out'})
    }
  })
}
const startAnimationLoop = () => {
  const calcPos = (el: HTMLElement) => {
    const rect = el.getBoundingClientRect()
    const cx = rect.left + rect.width / 2
    const cy = rect.top + rect.height / 3
    const dx = mouseRef.value.x - cx
    const dy = mouseRef.value.y - cy
    return {
      faceX: Math.max(-15, Math.min(15, dx / 20)),
      faceY: Math.max(-10, Math.min(10, dy / 30)),
      bodySkew: Math.max(-6, Math.min(6, -dx / 120))
    }
  }
  const calcEyePos = (el: HTMLElement, maxDist: number) => {
    const r = el.getBoundingClientRect()
    const cx = r.left + r.width / 2
    const cy = r.top + r.height / 2
    const dx = mouseRef.value.x - cx
    const dy = mouseRef.value.y - cy
    const dist = Math.min(Math.sqrt(dx ** 2 + dy ** 2), maxDist)
    const angle = Math.atan2(dy, dx)
    return {x: Math.cos(angle) * dist, y: Math.sin(angle) * dist}
  }
  const tick = () => {
    if (!quickToInstances) {
      return
    }
    const {
      isTyping: typing,
      isHidingPassword: hiding,
      isShowingPassword: showing,
      isLooking: looking,
    } = state
    if (purpleRef.value && !showing) {
      const pp = calcPos(purpleRef.value)
      if (typing || hiding) {
        quickToInstances.purpleSkew(pp.bodySkew - 12)
        quickToInstances.purpleX(40)
        quickToInstances.purpleHeight(440)
      } else {
        quickToInstances.purpleSkew(pp.bodySkew)
        quickToInstances.purpleX(0)
        quickToInstances.purpleHeight(400)
      }
    }
    if (blackRef.value && !showing) {
      const bp = calcPos(blackRef.value)
      if (looking) {
        quickToInstances.blackSkew(bp.bodySkew * 1.5 + 10)
        quickToInstances.blackX(20)
      } else if (typing || hiding) {
        quickToInstances.blackSkew(bp.bodySkew * 1.5)
        quickToInstances.blackX(0)
      } else {
        quickToInstances.blackSkew(bp.bodySkew)
        quickToInstances.blackX(0)
      }
    }
    if (orangeRef.value && !showing) {
      const op = calcPos(orangeRef.value);
      quickToInstances.orangeSkew(op.bodySkew);
    }
    if (yellowRef.value && !showing) {
      const op = calcPos(yellowRef.value);
      quickToInstances.yellowSkew(op.bodySkew);
    }
    if (purpleRef.value && !showing && !looking) {
      const pp = calcPos(purpleRef.value);
      const purpleFaceX = pp.faceX >= 0 ? Math.min(25, pp.faceX * 1.5) : pp.faceX;
      quickToInstances.purpleFaceLeft(45 + purpleFaceX);
      quickToInstances.purpleFaceTop(40 + pp.faceY);
    }
    if (blackRef.value && !showing && !looking) {
      const pp = calcPos(blackRef.value);
      quickToInstances.blackFaceLeft(26 + pp.faceX);
      quickToInstances.blackFaceTop(32 + pp.faceY);
    }
    if (orangeRef.value && !showing) {
      const op = calcPos(orangeRef.value);
      quickToInstances.orangeFaceX(op.faceX);
      quickToInstances.orangeFaceY(op.faceY);
    }
    if (yellowRef.value && !showing) {
      const op = calcPos(yellowRef.value);
      quickToInstances.yellowFaceX(op.faceX);
      quickToInstances.yellowFaceY(op.faceY);
    }
    if (yellowRef.value && !showing) {
      const yp = calcPos(yellowRef.value);
      quickToInstances.mouthX(yp.faceX);
      quickToInstances.mouthY(yp.faceY);
    }
    if (!showing) {
      const allPupils = containerRef.value?.querySelectorAll('.pupil');
      if (allPupils) {
        allPupils.forEach((p) => {
          const el = p as HTMLElement;
          const maxDist = Number(el.dataset.maxDistance) || 5;
          const ePos = calcEyePos(el, maxDist);
          gsap.set(el, {x: ePos.x, y: ePos.y});
        });
      }

      if (!looking) {
        const allEyeballs = containerRef.value?.querySelectorAll('.eyeball');
        if (allEyeballs) {
          allEyeballs.forEach((eb) => {
            const el = eb as HTMLElement;
            const maxDist = Number(el.dataset.maxDistance) || 10;
            const pupil = el.querySelector('.eyeball-pupil') as HTMLElement;
            if (!pupil) return;
            const ePos = calcEyePos(el, maxDist);
            gsap.set(pupil, {x: ePos.x, y: ePos.y});
          });
        }
      }
    }
    window.addEventListener('mousemove', onMove, {passive: true});
    rafIdRef.value = requestAnimationFrame(tick);
  }
  rafIdRef.value = requestAnimationFrame(tick);
}
const applyLookAtEachOther = () => {
  if (!quickToInstances || !purpleRef.value || !blackRef.value) return
  quickToInstances.purpleFaceLeft(55)
  quickToInstances.purpleFaceTop(65)
  quickToInstances.blackFaceLeft(32)
  quickToInstances.blackFaceTop(12)

  purpleRef.value?.querySelectorAll('.eyeball-pupil').forEach(p => {
    gsap.to(p, {x: 3, y: 4, duration: 0.3, ease: 'power2.out', overwrite: 'auto'})
  });
  blackRef.value?.querySelectorAll('.eyeball-pupil').forEach((p) => {
    gsap.to(p, {x: 0, y: -4, duration: 0.3, ease: 'power2.out', overwrite: 'auto'});
  });
}
const applyHidingPassword = () => {
  if (!quickToInstances || !purpleRef.value) return
  quickToInstances.purpleFaceLeft(55)
  quickToInstances.purpleFaceTop(65)
}
const applyShowPassword = () => {
  if (!quickToInstances) return
  quickToInstances.purpleSkew(0);
  quickToInstances.blackSkew(0);
  quickToInstances.orangeSkew(0);
  quickToInstances.yellowSkew(0);
  quickToInstances.purpleX(0);
  quickToInstances.blackX(0);
  quickToInstances.purpleHeight(400);

  quickToInstances.purpleFaceLeft(20);
  quickToInstances.purpleFaceTop(35);
  quickToInstances.blackFaceLeft(10);
  quickToInstances.blackFaceTop(28);
  quickToInstances.orangeFaceX(50 - 82);
  quickToInstances.orangeFaceY(85 - 90);
  quickToInstances.yellowFaceX(20 - 52);
  quickToInstances.yellowFaceY(35 - 40);
  quickToInstances.mouthX(10 - 40);
  quickToInstances.mouthY(0);
}
const clearTimeouts = () => {
  if (purpleBlinkTimerRef.value) clearTimeout(purpleBlinkTimerRef.value)
  if (blackBlinkTimerRef.value) clearTimeout(blackBlinkTimerRef.value)
  if (purplePeekTimerRef.value) clearTimeout(purplePeekTimerRef.value)
  if (lookingTimerRef.value) clearTimeout(lookingTimerRef.value)
}
const startBlinking = (refKey: Ref<HTMLElement | null>, timerRefKey: Ref<number | undefined>) => {
  const scheduleBlink = () => {
    const eyeballs = refKey.value?.querySelectorAll('.eyeball')
    if (!eyeballs?.length) return

    timerRefKey.value = setTimeout(() => {
      eyeballs.forEach((el) => {
        gsap.to(el, {height: 2, duration: 0.08, ease: 'power2.in'})
      })
      setTimeout(() => {
        eyeballs.forEach((el) => {
          const size = Number((el as HTMLElement).style.width.replace('px', '')) || 18;
          gsap.to(el, {height: size, duration: 0.08, ease: 'power2.out'})
        })
        scheduleBlink()
      }, 150)
    }, Math.random() * 4000 + 3000)
  }
  scheduleBlink()
}
watch(
    () => [props.isTyping, props.showPassword],
    () => {
      state.isTyping = props.isTyping
      state.isHidingPassword = isHidingPassword.value
      state.isShowingPassword = isShowingPassword.value

      if (props.isTyping && !state.isShowingPassword) {
        state.isLooking = true
        applyLookAtEachOther()
        clearTimeout(lookingTimerRef.value)
        lookingTimerRef.value = setTimeout(() => {
          state.isLooking = false
          purpleRef.value?.querySelectorAll('.eyeball-pupil').forEach(p => {
            gsap.killTweensOf(p)
          })
        }, 800)
      } else {
        clearTimeout(lookingTimerRef.value)
        state.isLooking = false
      }
    },
    {immediate: true}
)
watch(
    () => [isShowingPassword.value, props.passwordLength],
    () => {
      if (isShowingPassword.value) {
        applyShowPassword()
      } else if (state.isHidingPassword) {
        applyHidingPassword()
      }
    }
)
onMounted(() => {
  startBlinking(purpleRef, purpleBlinkTimerRef)
  startBlinking(blackRef, blackBlinkTimerRef)
})
onUnmounted(() => {
  clearTimeouts()
})
onMounted(() => {
  if (!containerRef.value) {
    return;
  }
  gsap.set('.pupil', {x: 0, y: 0})
  gsap.set('.eyeball-pupil', {x: 0, y: 0})
  initQuickTo()
  startAnimationLoop()
  const handleMouseMove = (e: MouseEvent) => {
    mouseRef.value.x = e.clientX
    mouseRef.value.y = e.clientY
  }
  window.addEventListener('mousemove', handleMouseMove, {passive: true})
  onUnmounted(() => {
    window.removeEventListener('mousemove', handleMouseMove)
    if (rafIdRef.value) cancelAnimationFrame(rafIdRef.value)
    clearTimeouts()
  })
})
</script>

<template>
  <div ref="containerRef" style="position: relative; width: 550px; height: 400px">
    <div ref="purpleRef"
         style="
        position: absolute; bottom: 0; left: 70px; width: 180px; height: 400px;
        background-color: #6C3FF5; border-radius: 10px 10px 0 0; z-index: 1;
        transform-origin: bottom center; will-change: transform;
      ">
      <div ref="purpleFaceRef"
           style="
          position: absolute; display: flex; gap: 32px;
          left: 45px; top: 40px;
        ">
        <eye-ball :size="18" :pupil-size="7" :max-distance="5" eye-color="white" :pupil-color="'#2D2D2D'"/>
        <eye-ball :size="18" :pupil-size="7" :max-distance="5" eye-color="white" :pupil-color="'#2D2D2D'"/>
      </div>
    </div>
    <div ref="blackRef"
         style="
        position: absolute; bottom: 0; left: 240px; width: 120px; height: 310px;
        background-color: #2D2D2D; border-radius: 8px 8px 0 0; z-index: 2;
        transform-origin: bottom center; will-change: transform;
      ">
      <div ref="blackFaceRef"
           style="
          position: absolute; display: flex; gap: 24px;
          left: 26px; top: 32px;
        ">
        <eye-ball :size="16" :pupil-size="6" :max-distance="4" eye-color="white" :pupil-color="'#2D2D2D'"/>
        <eye-ball :size="16" :pupil-size="6" :max-distance="4" eye-color="white" :pupil-color="'#2D2D2D'"/>
      </div>
    </div>
    <div ref="orangeRef"
         style="
         position: absolute; bottom: 0; left: 0; width: 240px; height: 200px;
        background-color: #FF9B6B; border-radius: 120px 120px 0 0; z-index: 3;
        transform-origin: bottom center; will-change: transform;
      ">
      <div ref="orangeFaceRef"
           style="
         position: absolute; display: flex; gap: 32px;
          left: 82px; top: 90px;
        ">
        <pupil :size="12" :max-distance="5" pupil-color="#2D2D2D"/>
        <pupil :size="12" :max-distance="5" pupil-color="#2D2D2D"/>
      </div>
    </div>
    <div ref="yellowRef"
         style="
         position: absolute; bottom: 0; left: 310px; width: 140px; height: 230px;
        background-color: #E8D754; border-radius: 70px 70px 0 0; z-index: 4;
        transform-origin: bottom center; will-change: transform;
      ">
      <div ref="yellowFaceRef"
           style="
          position: absolute; display: flex; gap: 24px;
          left: 52px; top: 40px;
        ">
        <pupil :size="12" :max-distance="5" pupil-color="#2D2D2D"/>
        <pupil :size="12" :max-distance="5" pupil-color="#2D2D2D"/>
      </div>
      <div ref="yellowMouthRef" style="
          position: absolute; width: 80px; height: 4px; background-color: #2D2D2D;
          border-radius: 9999px; left: 40px; top: 88px;
        "/>
    </div>
  </div>
</template>

<style scoped>

</style>